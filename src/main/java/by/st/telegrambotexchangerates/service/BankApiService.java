package by.st.telegrambotexchangerates.service;


import org.springframework.stereotype.Service;


import java.util.Arrays;
import java.util.List;

@Service
public class BankApiService {
    public List<String> getAllBanks() {
        return Arrays.asList("Нацбанк РБ", "Альфа-Банк", "Беларусбанк");
    }
}
