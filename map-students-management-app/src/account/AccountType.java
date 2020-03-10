package account;

/**
 * Enum for Account type
 * ADMINISTRATOR - can add/delete students
 * PROFESSOR - can add Homework - give grades
 * STUDENT - can only see grades, homework
 */
public enum AccountType {
    ADMINISTRATOR, PROFESSOR, STUDENT
}
