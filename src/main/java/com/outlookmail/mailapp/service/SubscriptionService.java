package com.outlookmail.mailapp.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.outlookmail.mailapp.model.ServiceSubscription;
import com.outlookmail.mailapp.repository.ServiceSubscriptionRepository;

@Service
@Transactional
public class SubscriptionService {
    private final ServiceSubscriptionRepository subscriptionRepository;
    private final EmailService emailService;

    @Value("${app.admin.email:admin@example.com}")
    private String adminEmail;

    private static final DateTimeFormatter FLEX_DMY = new DateTimeFormatterBuilder()
            .parseLenient()
            .appendValue(ChronoField.DAY_OF_MONTH)
            .appendLiteral('/')
            .appendValue(ChronoField.MONTH_OF_YEAR)
            .appendLiteral('/')
            .appendValue(ChronoField.YEAR, 4)
            .toFormatter(Locale.forLanguageTag("vi"));

    @Autowired
    public SubscriptionService(ServiceSubscriptionRepository subscriptionRepository,
                               EmailService emailService) {
        this.subscriptionRepository = subscriptionRepository;
        this.emailService = emailService;
    }

    public ServiceSubscription save(ServiceSubscription sub) {
        return subscriptionRepository.save(sub);
    }

    public void deleteById(Long id) {
        subscriptionRepository.deleteById(id);
    }

    public ServiceSubscription findById(Long id) {
        return subscriptionRepository.findById(id).orElse(null);
    }

    public List<ServiceSubscription> findAll() {
        return subscriptionRepository.findAll();
    }

    public List<ServiceSubscription> search(String q, String status, String before) {
        List<ServiceSubscription> all = subscriptionRepository.findAll();
        return all.stream()
                .filter(s -> {
                    if (q == null || q.isBlank()) return true;
                    String needle = q.toLowerCase();
                    return (s.getCustomerEmail() != null && s.getCustomerEmail().toLowerCase().contains(needle))
                            || (s.getServiceName() != null && s.getServiceName().toLowerCase().contains(needle))
                            || (s.getContactZalo() != null && s.getContactZalo().toLowerCase().contains(needle))
                            || (s.getContactInstagram() != null && s.getContactInstagram().toLowerCase().contains(needle));
                })
                .filter(s -> {
                    if (status == null || status.isBlank()) return true;
                    if ("notified".equalsIgnoreCase(status)) return s.isPreExpiryNotified();
                    if ("pending".equalsIgnoreCase(status)) return !s.isPreExpiryNotified();
                    return true;
                })
                .filter(s -> {
                    if (before == null || before.isBlank()) return true;
                    try {
                        LocalDate cutoff = LocalDate.parse(normalizeDate(before), FLEX_DMY);
                        return s.getEndDate() != null && (s.getEndDate().isBefore(cutoff) || s.getEndDate().isEqual(cutoff));
                    } catch (Exception e) {
                        return true;
                    }
                })
                .collect(Collectors.toList());
    }

    public List<ServiceSubscription> findEndingTomorrow() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        return subscriptionRepository.findByEndDate(tomorrow);
    }

    public List<ServiceSubscription> findEndingToday() {
        LocalDate today = LocalDate.now();
        return subscriptionRepository.findByEndDate(today);
    }

    public void notifyCustomersEndingTomorrow() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        List<ServiceSubscription> due = subscriptionRepository.findEndingOnAndNotNotified(tomorrow);
        if (due.isEmpty()) return;
        for (ServiceSubscription s : due) {
            emailService.sendSubscriptionExpiryReminderToCustomer(
                s.getCustomerEmail(),
                s.getServiceName(),
                s.getEndDate()
            );
            s.setPreExpiryNotified(true);
            subscriptionRepository.save(s);
        }
        emailService.sendSubscriptionExpiryDigestToAdmin(adminEmail, due);
    }

    public void sendAdminDigestForToday() {
        List<ServiceSubscription> dueToday = findEndingToday();
        if (dueToday.isEmpty()) return;
        emailService.sendSubscriptionExpiryTodayDigestToAdmin(adminEmail, dueToday);
    }

    public boolean sendReminderNow(Long subscriptionId) {
        Optional<ServiceSubscription> opt = subscriptionRepository.findById(subscriptionId);
        if (opt.isEmpty()) return false;
        ServiceSubscription s = opt.get();
        emailService.sendSubscriptionExpiryReminderToCustomer(
            s.getCustomerEmail(),
            s.getServiceName(),
            s.getEndDate()
        );
        // Mark as notified after manual send
        s.setPreExpiryNotified(true);
        subscriptionRepository.save(s);
        return true;
    }

    public ImportResult importFromText(String rawText) {
        if (rawText == null || rawText.trim().isEmpty()) {
            return new ImportResult(0, 0, List.of("Dữ liệu trống"));
        }
        String[] lines = rawText.replace("\r", "").split("\n");
        int ok = 0;
        int fail = 0;
        List<String> errors = new ArrayList<>();
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.isEmpty()) continue; // skip empty lines
            // Split by tab first, fallback to 2+ spaces
            String[] parts = line.split("\t");
            if (parts.length < 4) {
                parts = line.split("\s{2,}");
            }
            if (parts.length < 4) {
                // Try single spaces but limit to 4 tokens
                parts = line.split("\s+");
                if (parts.length < 4) {
                    fail++;
                    errors.add("Dòng " + (i + 1) + ": Không đúng định dạng (cần 4 cột: email, dịch vụ, bắt đầu, kết thúc)");
                    continue;
                }
            }
            String email = parts[0].trim();
            String service = parts[1].trim();
            String startStr = parts[2].trim();
            String endStr = parts[3].trim();
            if (email.isEmpty()) {
                // Skip rows without email (the user sample has blank email lines)
                continue;
            }
            try {
                LocalDate start = LocalDate.parse(normalizeDate(startStr), FLEX_DMY);
                LocalDate end = LocalDate.parse(normalizeDate(endStr), FLEX_DMY);
                ServiceSubscription s = new ServiceSubscription();
                s.setCustomerEmail(email);
                s.setServiceName(service);
                s.setStartDate(start);
                s.setEndDate(end);
                subscriptionRepository.save(s);
                ok++;
            } catch (Exception e) {
                fail++;
                errors.add("Dòng " + (i + 1) + ": " + e.getMessage());
            }
        }
        return new ImportResult(ok, fail, errors);
    }

    private String normalizeDate(String input) {
        // Accept formats like 19/4/2025 or 19/04/2025
        return input.replace('-', '/').replace('.', '/').trim();
    }

    public static class ImportResult {
        public final int success;
        public final int failed;
        public final List<String> errors;
        public ImportResult(int success, int failed, List<String> errors) {
            this.success = success;
            this.failed = failed;
            this.errors = errors;
        }
    }
} 