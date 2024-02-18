package by.st.telegrambotexchangerates.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Guest {
    private Long id;
    private String userName;
    private String firstName;
    private String lastName;
}
