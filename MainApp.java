package QuizAppGUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainApp::new);
    }

    public MainApp() {
        new RegistrationWindow();
    }
}

// Registration Window
class RegistrationWindow extends JFrame {
    private JTextField nameField, emailField;

    public RegistrationWindow() {
        setTitle("IQchecker");
        setSize(450, 350);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Setting background color and font color
        getContentPane().setBackground(new Color(0x2E3440)); // Dark background

        JLabel nameLabel = new JLabel("Enter Name:");
        nameLabel.setForeground(new Color(0xD8DEE9)); // Light text color
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(nameLabel, gbc);

        nameField = new JTextField(20);
        nameField.setFont(new Font("Arial", Font.PLAIN, 16));
        nameField.setBackground(new Color(0x4C566A)); // Darker input field background
        nameField.setForeground(Color.WHITE); // White text
        gbc.gridx = 1;
        add(nameField, gbc);

        JLabel emailLabel = new JLabel("Enter Age:");
        emailLabel.setForeground(new Color(0xD8DEE9));
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(emailLabel, gbc);

        emailField = new JTextField(20);
        emailField.setFont(new Font("Arial", Font.PLAIN, 16));
        emailField.setBackground(new Color(0x4C566A));
        emailField.setForeground(Color.WHITE);
        gbc.gridx = 1;
        add(emailField, gbc);

        // Register Button
        JButton registerButton = new JButton("Start Quiz");
        registerButton.setFont(new Font("Arial", Font.BOLD, 18));
        registerButton.setBackground(new Color(0x5E81AC)); // Soft blue color
        registerButton.setForeground(Color.WHITE);
        registerButton.addActionListener(e -> registerUser());
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        registerButton.setPreferredSize(new Dimension(150, 40));
        add(registerButton, gbc);

        setVisible(true);
        setLocationRelativeTo(null); // Center the window on the screen
    }

    private void registerUser() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();

        if (name.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields!", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Registration Successful!", "Welcome", JOptionPane.INFORMATION_MESSAGE);
            new QuizWindow(name);  // Open the quiz window
            dispose();  // Close the registration window
        }
    }
}

// Quiz Window
class QuizWindow extends JFrame {
    private JLabel questionLabel, timerLabel, userNameLabel;
    private JButton[] optionButtons;
    private int currentQuestionIndex = 0;
    private int timeLeft = 15;
    private Timer quizTimer;
    private ArrayList<Question> questions;
    private int correctAnswers = 0;
    private String userName;

    public QuizWindow(String userName) {
        this.userName = userName;
        questions = getIQQuestions();

        setTitle("IQ Quiz - QuizAppGUI");
        setSize(600, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        getContentPane().setBackground(new Color(0x2E3440)); // Dark background
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // User name label
        userNameLabel = new JLabel("Welcome, " + userName);
        userNameLabel.setFont(new Font("Arial", Font.BOLD, 20));
        userNameLabel.setForeground(new Color(0x88C0D0)); // Light Blue
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(userNameLabel, gbc);

        // Timer label
        timerLabel = new JLabel("Time: 15 sec");
        timerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        timerLabel.setForeground(new Color(0xBF616A)); // Red Timer Color
        gbc.gridy = 1;
        add(timerLabel, gbc);

        // Question label
        questionLabel = new JLabel();
        questionLabel.setFont(new Font("Arial", Font.BOLD, 18));
        questionLabel.setForeground(Color.WHITE);
        questionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        add(questionLabel, gbc);

        // Option buttons
        optionButtons = new JButton[4];
        gbc.gridwidth = 2;
        for (int i = 0; i < 4; i++) {
            optionButtons[i] = new JButton();
            optionButtons[i].setFont(new Font("Arial", Font.PLAIN, 16));
            optionButtons[i].setBackground(new Color(0x4C566A));
            optionButtons[i].setForeground(Color.WHITE);
            final int index = i;
            optionButtons[i].addActionListener(e -> handleAnswer(index));
            gbc.gridy = i + 3;
            add(optionButtons[i], gbc);
        }

        // Submit button
        JButton submitButton = new JButton("Next Question");
        submitButton.setFont(new Font("Arial", Font.BOLD, 16));
        submitButton.setBackground(new Color(0x5E81AC));
        submitButton.setForeground(Color.WHITE);
        submitButton.addActionListener(e -> moveToNextQuestion());
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        add(submitButton, gbc);

        startQuiz();
        setVisible(true);
        setLocationRelativeTo(null);
    }

    private void startQuiz() {
        displayQuestion(currentQuestionIndex);
        startTimer();
    }

    private void startTimer() {
        quizTimer = new Timer();
        quizTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                timeLeft--;
                timerLabel.setText("Time: " + timeLeft + " sec");

                if (timeLeft <= 0) {
                    moveToNextQuestion();
                }
            }
        }, 1000, 1000);
    }

    private void displayQuestion(int index) {
        Question currentQuestion = questions.get(index);
        questionLabel.setText("<html>" + currentQuestion.getQuestionText() + "</html>");
        for (int i = 0; i < optionButtons.length; i++) {
            optionButtons[i].setText(currentQuestion.getOptions()[i]);
        }
        timeLeft = 15;
    }

    private void handleAnswer(int selectedOption) {
        quizTimer.cancel();
        Question current = questions.get(currentQuestionIndex);

        if (selectedOption == current.getCorrectOption()) {
            correctAnswers++;
            JOptionPane.showMessageDialog(this, "Correct!", "Answer", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Incorrect. Correct Answer: " + current.getCorrectAnswer(), "Answer", JOptionPane.WARNING_MESSAGE);
        }
        moveToNextQuestion();
    }

    private void moveToNextQuestion() {
        quizTimer.cancel();
        currentQuestionIndex++;

        if (currentQuestionIndex < questions.size()) {
            displayQuestion(currentQuestionIndex);
            startTimer();
        } else {
            new FinalResultsWindow(correctAnswers, questions.size(), userName);
            dispose();
        }
    }

    private ArrayList<Question> getIQQuestions() {
        ArrayList<Question> sampleQuestions = new ArrayList<>();
        sampleQuestions.add(new Question("If two pencils cost 7 cents, how much do 10 pencils cost?", new String[]{"35 cents", "70 cents", "7 cents", "50 cents"}, 1));
        sampleQuestions.add(new Question("What is the missing number in the series: 1, 2, 4, 8, __?", new String[]{"16", "10", "12", "15"}, 0));
        sampleQuestions.add(new Question("Which number is 1000 more than 999?", new String[]{"1999", "1000", "9999", "2000"}, 3));
        sampleQuestions.add(new Question("Which is heavier: 1000 grams of cotton or 1000 grams of iron?", new String[]{"Cotton", "Iron", "Both are equal", "None"}, 2));
        sampleQuestions.add(new Question("How many months have 28 days?", new String[]{"1", "12", "7", "10"}, 1));
        sampleQuestions.add(new Question("A plane crashes on the border of the United States and Canada. Where do they bury the survivors?", new String[]{"United States", "Canada", "Neither", "Both"}, 2));
        return sampleQuestions;
    }
}

// Final Results Window
class FinalResultsWindow extends JFrame {
    public FinalResultsWindow(int correctAnswers, int totalQuestions, String userName) {
        setTitle("Quiz Results - " + userName);
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Background and Text Styling
        getContentPane().setBackground(new Color(0x2E3440));

        int score = (int) (((double) correctAnswers / totalQuestions) * 100);
        String iqLevel = "";
        String careerAdvice = "";
        if (score >= 80) {
            iqLevel = "High IQ ";
            careerAdvice = "You are suited for leadership and decision-making roles!" +
                    "Politician/Policy Maker,CEO/Executive Roles,Entrepreneur,";
        } else if (score >= 50) {
            iqLevel = "Average IQ";
            careerAdvice = "You are suited for roles involving analysis and creativity!"+
                    "Data Scientist/Analyst,Business Analyst,Architect";
        } else {
            iqLevel = "Low IQ";
            careerAdvice = "You might want to focus on roles that require hands-on learning and practice!+" +
                    "Pilot or Aviation Technician,Physical Therapist,Photographer/Videographer";
        }

        JLabel resultLabel = new JLabel(userName + ", your score: " + score + "%");
        resultLabel.setForeground(new Color(0x88C0D0));
        resultLabel.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(resultLabel, gbc);

        JLabel iqLabel = new JLabel("IQ Level: " + iqLevel);
        iqLabel.setForeground(new Color(0x88C0D0));
        iqLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridy = 1;
        add(iqLabel, gbc);

        JLabel adviceLabel = new JLabel("Career Advice: " + careerAdvice);
        adviceLabel.setForeground(new Color(0x88C0D0));
        adviceLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridy = 2;
        add(adviceLabel, gbc);

        // Play Again Button
        JButton playAgainButton = new JButton("Play Again");
        playAgainButton.setFont(new Font("Arial", Font.BOLD, 16));
        playAgainButton.setBackground(new Color(0x5E81AC));
        playAgainButton.setForeground(Color.WHITE);
        playAgainButton.addActionListener(e -> new RegistrationWindow());
        gbc.gridy = 3;
        add(playAgainButton, gbc);

        // Exit Button
        JButton exitButton = new JButton("Exit");
        exitButton.setFont(new Font("Arial", Font.BOLD, 16));
        exitButton.setBackground(new Color(0xBF616A)); // Red background
        exitButton.setForeground(Color.WHITE);
        exitButton.addActionListener(e -> System.exit(0));
        gbc.gridy = 4;
        add(exitButton, gbc);

        setVisible(true);
        setLocationRelativeTo(null);
    }
}

// Question class
class Question {
    private String questionText;
    private String[] options;
    private int correctOption;

    public Question(String questionText, String[] options, int correctOption) {
        this.questionText = questionText;
        this.options = options;
        this.correctOption = correctOption;
    }

    public String getQuestionText() {
        return questionText;
    }

    public String[] getOptions() {
        return options;
    }

    public int getCorrectOption() {
        return correctOption;
    }

    public String getCorrectAnswer() {
        return options[correctOption];
    }
}

