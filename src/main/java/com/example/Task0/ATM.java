package com.example.Task0;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ATM {

    private Logger logger = LoggerFactory.getLogger(ATM.class);

    @Value("${banknote.list}")
    private String banknoteListProperty;

    private static Set<Integer> BANKNOTES_SET;

    @PostConstruct
    public void init() {
        try {
            BANKNOTES_SET = Arrays.stream(banknoteListProperty.split(","))
                    .map(String::trim)
                    .map(Integer::parseInt)
                    .collect(Collectors.toSet());
            askAndGetInput();
        } catch (Exception e) {
            logger.error("Unable to initialize ATM: {}", e.toString());
        }
    }

    public String getNumberOfBanknotes(String[] input) {
        if (input == null || input.length < 2) throw new IllegalArgumentException("Incorrect input");
        int sum = getPositiveInteger(input[0]);
        List<Integer> requiredBanknotesList = new ArrayList<>();
        for (int i = 1; i < input.length; i++) {
            int banknote = getPositiveInteger(input[i]);
            if (!BANKNOTES_SET.contains(banknote))
                throw new IllegalArgumentException("There is no such banknote: " + banknote);
            requiredBanknotesList.add(banknote);
        }
        requiredBanknotesList.sort(Comparator.reverseOrder());
        return calculate(sum, requiredBanknotesList, "");
    }

    private int getPositiveInteger(String input) {
        if (input == null || input.isEmpty()) throw new IllegalArgumentException("Unable to parse integer: " + input);
        try {
            int output = Integer.parseInt(input);
            if (output < 1) throw new IllegalArgumentException("Value can't be zero or negative: " + input);
            return output;
        }
       catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid value: " + input);
       }
    }

    private String calculate(int sum, List<Integer> requiredBanknotesList, String result) {
        if (sum == 0) return result;
        if (requiredBanknotesList.isEmpty()) throw new RuntimeException("Can't give you the sum with these banknotes");
        int banknote = requiredBanknotesList.get(0);
        int banknotes = sum / banknote;
        requiredBanknotesList.remove(0);
        if (banknotes > 0) {
            return calculate(sum - banknotes * banknote, requiredBanknotesList, result + banknote + " - " + banknotes + ", ");
        }
        return calculate(sum, requiredBanknotesList, result);
    }

    public void askAndGetInput() {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Введите сумму и банкноты через запятую:");
            String input = scanner.nextLine();
            String result = getNumberOfBanknotes(input.split(",")).replaceFirst(", $", "");
            System.out.println(result);
        } catch (Exception e) {
            logger.error("Ошибка: ", e);
            System.out.println(e.getMessage());
        }
    }

}
