package common.ui;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class DatePicker extends JPanel {
    private final JComboBox<Integer> yearBox;
    private final JComboBox<String> monthBox;
    private final JComboBox<Integer> dayBox;
    private final JButton calendarButton = new JButton("\uD83D\uDCC5"); // 📅 calendar icon

    private static final String[] MONTH_NAMES = {
        "01 - Jan", "02 - Feb", "03 - Mar", "04 - Apr", "05 - May", "06 - Jun",
        "07 - Jul", "08 - Aug", "09 - Sep", "10 - Oct", "11 - Nov", "12 - Dec"
    };

    private Runnable onDateChanged;

    public DatePicker() {
        this(LocalDate.now().minusYears(20)); // Default to 20 years ago for typical birthday
    }

    public DatePicker(LocalDate initialDate) {
        setLayout(new FlowLayout(FlowLayout.LEFT, 4, 0));
        setOpaque(false);

        int maxYear = LocalDate.now().getYear() + 5;
        Integer[] years = new Integer[maxYear - 1920 + 1];
        for (int i = 0; i < years.length; i++) {
            years[i] = maxYear - i; // Latest year first
        }

        yearBox = new JComboBox<>(years);
        monthBox = new JComboBox<>(MONTH_NAMES);
        dayBox = new JComboBox<>();

        updateDays();

        if (initialDate != null) {
            setDate(initialDate);
        }

        yearBox.addActionListener(e -> {
            updateDays();
            notifyDateChanged();
        });
        monthBox.addActionListener(e -> {
            updateDays();
            notifyDateChanged();
        });
        dayBox.addActionListener(e -> notifyDateChanged());

        calendarButton.setToolTipText("Open Calendar Picker");
        calendarButton.setMargin(new Insets(2, 6, 2, 6));
        calendarButton.addActionListener(e -> openCalendarDialog());

        add(new JLabel("Y:"));
        add(yearBox);
        add(new JLabel("M:"));
        add(monthBox);
        add(new JLabel("D:"));
        add(dayBox);
        add(calendarButton);
    }

    public void setOnDateChanged(Runnable onDateChanged) {
        this.onDateChanged = onDateChanged;
    }

    private void notifyDateChanged() {
        if (onDateChanged != null) {
            onDateChanged.run();
        }
    }

    private void updateDays() {
        Integer selectedYear = (Integer) yearBox.getSelectedItem();
        int selectedMonthIndex = monthBox.getSelectedIndex();

        if (selectedYear == null || selectedMonthIndex < 0) return;

        int selectedMonth = selectedMonthIndex + 1;
        YearMonth ym = YearMonth.of(selectedYear, selectedMonth);
        int daysInMonth = ym.lengthOfMonth();

        Integer currentSelectedDay = (Integer) dayBox.getSelectedItem();

        dayBox.removeAllItems();
        for (int d = 1; d <= daysInMonth; d++) {
            dayBox.addItem(d);
        }

        if (currentSelectedDay != null && currentSelectedDay <= daysInMonth) {
            dayBox.setSelectedItem(currentSelectedDay);
        } else {
            dayBox.setSelectedIndex(0);
        }
    }

    public LocalDate getSelectedDate() {
        Integer year = (Integer) yearBox.getSelectedItem();
        int month = monthBox.getSelectedIndex() + 1;
        Integer day = (Integer) dayBox.getSelectedItem();

        if (year == null || day == null) {
            return LocalDate.now();
        }
        return LocalDate.of(year, month, day);
    }

    public String getDateString() {
        LocalDate date = getSelectedDate();
        return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public void setDate(LocalDate date) {
        if (date == null) return;
        yearBox.setSelectedItem(date.getYear());
        monthBox.setSelectedIndex(date.getMonthValue() - 1);
        updateDays();
        dayBox.setSelectedItem(date.getDayOfMonth());
    }

    public void setDateString(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) return;
        try {
            LocalDate d = LocalDate.parse(dateString.trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            setDate(d);
        } catch (Exception ignored) {}
    }

    private void openCalendarDialog() {
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        JDialog dialog;
        if (parentWindow instanceof Frame) {
            dialog = new JDialog((Frame) parentWindow, "Select Date", true);
        } else if (parentWindow instanceof Dialog) {
            dialog = new JDialog((Dialog) parentWindow, "Select Date", true);
        } else {
            dialog = new JDialog();
            dialog.setModal(true);
            dialog.setTitle("Select Date");
        }

        LocalDate current = getSelectedDate();
        final int[] viewYear = {current.getYear()};
        final int[] viewMonth = {current.getMonthValue()};

        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(340, 300);
        dialog.setLocationRelativeTo(this);

        JPanel headerPanel = new JPanel(new BorderLayout(5, 5));
        JButton prevBtn = new JButton("<");
        JButton nextBtn = new JButton(">");
        JLabel monthYearLabel = new JLabel("", SwingConstants.CENTER);
        monthYearLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

        headerPanel.add(prevBtn, BorderLayout.WEST);
        headerPanel.add(monthYearLabel, BorderLayout.CENTER);
        headerPanel.add(nextBtn, BorderLayout.EAST);

        JPanel daysPanel = new JPanel(new GridLayout(7, 7, 2, 2));

        Runnable renderCalendar = () -> {
            daysPanel.removeAll();
            YearMonth ym = YearMonth.of(viewYear[0], viewMonth[0]);
            monthYearLabel.setText(ym.getMonth().name() + " " + viewYear[0]);

            String[] dayHeaders = {"Su", "Mo", "Tu", "We", "Th", "Fr", "Sa"};
            for (String dh : dayHeaders) {
                JLabel lbl = new JLabel(dh, SwingConstants.CENTER);
                lbl.setFont(new Font("SansSerif", Font.BOLD, 12));
                lbl.setForeground(Color.GRAY);
                daysPanel.add(lbl);
            }

            LocalDate firstOfMonth = ym.atDay(1);
            int startDayOfWeek = firstOfMonth.getDayOfWeek().getValue() % 7; // Sunday = 0
            int daysCount = ym.lengthOfMonth();

            for (int i = 0; i < startDayOfWeek; i++) {
                daysPanel.add(new JLabel(""));
            }

            for (int day = 1; day <= daysCount; day++) {
                final int selectedDay = day;
                JButton dayBtn = new JButton(String.valueOf(day));
                dayBtn.setMargin(new Insets(2, 2, 2, 2));
                if (viewYear[0] == current.getYear() && viewMonth[0] == current.getMonthValue() && day == current.getDayOfMonth()) {
                    dayBtn.setBackground(new Color(180, 220, 255));
                }
                dayBtn.addActionListener(ev -> {
                    LocalDate picked = LocalDate.of(viewYear[0], viewMonth[0], selectedDay);
                    setDate(picked);
                    notifyDateChanged();
                    dialog.dispose();
                });
                daysPanel.add(dayBtn);
            }

            daysPanel.revalidate();
            daysPanel.repaint();
        };

        prevBtn.addActionListener(e -> {
            viewMonth[0]--;
            if (viewMonth[0] < 1) {
                viewMonth[0] = 12;
                viewYear[0]--;
            }
            renderCalendar.run();
        });

        nextBtn.addActionListener(e -> {
            viewMonth[0]++;
            if (viewMonth[0] > 12) {
                viewMonth[0] = 1;
                viewYear[0]++;
            }
            renderCalendar.run();
        });

        renderCalendar.run();

        dialog.add(headerPanel, BorderLayout.NORTH);
        dialog.add(daysPanel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }
}
