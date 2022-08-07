package rs.ac.ni.pmf.thequiz.model;

import java.util.Arrays;
import java.util.List;

public class QuestionsRepository {
    private static final List<Question> QUESTIONS = Arrays.asList(
            new Question("Question1", "Correct answer", "Bad answer 1", "Bad answer 2", 1),
            new Question("Question2", "Bad answer 1", "Correct answer", "Bad answer 2", 2),
            new Question("Question3", "Correct answer", "Bad answer 1", "Bad answer 2", 1),
            new Question("Question4", "Bad answer 1", "Bad answer 2", "Correct answer", 3),
            new Question("Question5", "Bad answer 1", "Bad answer 2", "Correct answer", 3)
    );

    public static List<Question> findAll() {
        return QUESTIONS;
    }
}