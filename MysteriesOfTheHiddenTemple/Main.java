import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Set;
import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

public class Main extends JFrame implements ActionListener, KeyListener {

    private JLabel textLabel;
    private JButton solveButton, startFightButton, attackButton, blockButton;
    private Set<String> solvedRiddles;
    private boolean bossRoomUnlocked;
    private boolean gameStarted;
    private String leftHint = "For the Room in the East: we are a lot of right and lefts! (THIS HINT ONLY APPEARS ONCE!)";
    private String rightHint = "For the Room in the North: The answer is right at your fingertips! (THIS HINT ONLY APPEARS ONCE!)";
    private String upHint = "For the Room in the West: In the darkness we work best! (THIS HINT ONLY APPEARS ONCE!)";
    private boolean playerBlocked = false;
    

    // Instance fields for health
    private int playerHealth;
    private int bossHealth;

    
    private JLabel monsterLabel;
    
    private JLabel northLabel;
    private JLabel westLabel;
    private JLabel eastLabel;

    public Main() {
        setTitle("Mysteries of the Secret Temple");
        setSize(720, 576);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Background color for the window
        getContentPane().setBackground(new Color(34, 34, 34));

        // Main story label styling
        textLabel = new JLabel("", SwingConstants.CENTER);
        textLabel.setFont(new Font("Serif", Font.PLAIN, 18));
        textLabel.setVerticalAlignment(SwingConstants.CENTER);
        textLabel.setForeground(Color.WHITE); // White text for readability
        textLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
       
        JPanel textImagePanel = new JPanel(new BorderLayout());
        textImagePanel.setOpaque(false);

        textImagePanel.add(textLabel, BorderLayout.CENTER);


        

        monsterLabel = new JLabel();
        monsterLabel.setVisible(false);
        monsterLabel.setHorizontalAlignment(SwingConstants.CENTER);
        textImagePanel.add(monsterLabel, BorderLayout.SOUTH);


        northLabel = new JLabel();
        northLabel.setVisible(false);
        northLabel.setHorizontalAlignment(SwingConstants.CENTER);
        textImagePanel.add(northLabel, BorderLayout.NORTH);

        westLabel = new JLabel();
        westLabel.setVisible(false);
        westLabel.setHorizontalAlignment(SwingConstants.CENTER);
        textImagePanel.add(westLabel, BorderLayout.WEST);

        eastLabel = new JLabel();
        eastLabel.setVisible(false);
        eastLabel.setHorizontalAlignment(SwingConstants.CENTER);
        textImagePanel.add(eastLabel, BorderLayout.EAST);
        
        add(textImagePanel, BorderLayout.CENTER);


        ImageIcon northIcon = new ImageIcon(getClass().getResource("north.jpg"));
        Image northImage = northIcon.getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH);
        northLabel.setIcon(new ImageIcon(northImage));
    
        ImageIcon eastIcon = new ImageIcon(getClass().getResource("east.jpg"));
        Image eastImage = eastIcon.getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH);
        eastLabel.setIcon(new ImageIcon(eastImage));

        ImageIcon westIcon = new ImageIcon(getClass().getResource("west.jpg"));
        Image westImage = westIcon.getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH);
        westLabel.setIcon(new ImageIcon(westImage));

        ImageIcon bossIcon = new ImageIcon(getClass().getResource("monster.png"));
        Image bossImage = bossIcon.getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH);
        monsterLabel.setIcon(new ImageIcon(bossImage));

        

        // Boss health label styling
        
        JPanel buttonPanel = new JPanel();
        add(buttonPanel, BorderLayout.SOUTH);

        // Solve button styling
        solveButton = new JButton("Solve Riddle");
        solveButton.setVisible(false);
        solveButton.setBackground(new Color(0, 128, 0)); // Green button
        solveButton.setForeground(Color.WHITE);
        solveButton.setFont(new Font("Serif", Font.BOLD, 18));
        solveButton.setBorder(BorderFactory.createRaisedBevelBorder());
        solveButton.addActionListener(this);
        add(solveButton, BorderLayout.NORTH);

        // Start fight button styling
        startFightButton = new JButton("Start Fight");
        startFightButton.setVisible(false); // Initially hidden
        startFightButton.setBackground(new Color(255, 69, 0)); // Red button
        startFightButton.setForeground(Color.WHITE);
        startFightButton.setFont(new Font("Serif", Font.BOLD, 18));
        startFightButton.setBorder(BorderFactory.createRaisedBevelBorder());
        startFightButton.addActionListener(this);
        add(startFightButton, BorderLayout.SOUTH);

        // Attack button styling
        attackButton = new JButton("Attack");
        attackButton.setVisible(false); // Initially hidden
        attackButton.setBackground(new Color(0, 123, 255)); // Blue button
        attackButton.setForeground(Color.WHITE);
        attackButton.setFont(new Font("Serif", Font.BOLD, 20));
        attackButton.setBorder(BorderFactory.createRaisedBevelBorder());
        attackButton.addActionListener(this);
        add(attackButton, BorderLayout.WEST);

        // Block button styling
        blockButton = new JButton("Block");
        blockButton.setVisible(false); // Initially hidden
        blockButton.setBackground(new Color(255, 69, 0)); // Red button
        blockButton.setForeground(Color.WHITE);
        blockButton.setFont(new Font("Serif", Font.BOLD, 20));
        blockButton.setBorder(BorderFactory.createRaisedBevelBorder());
        blockButton.addActionListener(this);
        add(blockButton, BorderLayout.EAST);

        startFightButton.addActionListener(e -> startBossFight());


        musicManager();
        addKeyListener(this); 
        setFocusable(true);   

        startIntroduction(); 
    }

    private Clip templeMusic;
    private Clip bossMusic;
    private Clip victoryMusic;

    public void musicManager() {
        try {
            URL templeMusicUrl = getClass().getResource("temple.wav");
            AudioInputStream templeMusicStream = AudioSystem.getAudioInputStream(templeMusicUrl);
            templeMusic = AudioSystem.getClip();
            templeMusic.open(templeMusicStream);

            URL bossMusicUrl = getClass().getResource("Boss.wav");
            AudioInputStream bossMusicStream = AudioSystem.getAudioInputStream(bossMusicUrl);
            bossMusic = AudioSystem.getClip();
            bossMusic.open(bossMusicStream);

            URL victoryMusicUrl = getClass().getResource("victory.wav");
            AudioInputStream victoryMusicStream = AudioSystem.getAudioInputStream(victoryMusicUrl);
            victoryMusic = AudioSystem.getClip();
            victoryMusic.open(victoryMusicStream);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void playTempleMusic() {
        if(templeMusic != null) {
            templeMusic.loop(Clip.LOOP_CONTINUOUSLY);
        }
        
    }

    public void stopTempleMusic() {
        if(templeMusic != null) {
            templeMusic.stop();
        }
    }

    public void playBossMusic() {
        if( bossMusic != null) {
            bossMusic.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public void stopBossMusic() {
        if(bossMusic != null) {
            bossMusic.stop();
        }
    
    }

    public void playVictoryMusic() {
        if(victoryMusic != null) {
            victoryMusic.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }


    public void stopVictoryMusic() {
        if(victoryMusic != null) {
            victoryMusic.stop();
        }
    }
    private void startIntroduction() {
        gameStarted = false;

        ImageIcon templeIcon = new ImageIcon(getClass().getResource("temple2.jpg"));

        Image resizedImage = templeIcon.getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH);
        ImageIcon resizedIcon= new ImageIcon(resizedImage);
        int option = JOptionPane.showOptionDialog(
            this,
            "After years of searching, you stand before the ancient temple. Do you dare to enter?",
            "Mysteries of the Secret Temple",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            resizedIcon,
            new String[]{"Enter", "Turn Back"},
            "Enter"
        );

        if (option == JOptionPane.YES_OPTION) {
            initializeGame();
        } else {
            displayStory("You decide to turn back, leaving the mysteries of the temple unsolved. Game Over.");
            JOptionPane.showMessageDialog(this, "The game will now close. Goodbye!");
            System.exit(0);
        }
    }

    private void initializeGame() {
        attackButton.setVisible(false);
        blockButton.setVisible(false);
        monsterLabel.setVisible(false);
        victoryMusic.stop();
        solvedRiddles = new HashSet<>();
        bossRoomUnlocked = false;
        gameStarted = true;
        playTempleMusic();
        
        
        displayStory("You are now in the temple. Use the arrow keys to choose your direction.");
    }

    private void displayStory(String text) {
        textLabel.setText("<html>" + text + "</html>");

    }

    private void navigate(String direction) {
        if (!gameStarted) return;


        String roomDescription;
        String hint = "";

        switch (direction) {
            case "up":

                roomDescription = "You head up and enter a room with ancient carvings on the walls.";
                hint = upHint;
                westLabel.setVisible(false);
                eastLabel.setVisible(false);
                northLabel.setVisible(true);
                upHint = ""; // Hint used
                handleRiddleRoom("Room to the North", roomDescription, "What has keys but can’t open locks?", "keyboard", hint);
                break;
            case "left":
                roomDescription = "You turn left and find a room with flickering torches and a mysterious chest.";
                hint = leftHint;
                northLabel.setVisible(false);
                eastLabel.setVisible(false);
                westLabel.setVisible(true);
                
                leftHint = ""; // Hint used
                handleRiddleRoom("Room to the West", roomDescription, "I’m tall when I’m young, and I’m short when I’m old. What am I?", "candle", hint);
                break;
            case "right":
                roomDescription = "You move right and find a dusty library filled with scrolls and books.";
                hint = rightHint;
                northLabel.setVisible(false);
                westLabel.setVisible(false);
                eastLabel.setVisible(true);
                rightHint = ""; // Hint used
                handleRiddleRoom("Room to the East", roomDescription, "The more of this you take, the more you leave behind. What is it?", "footsteps", hint);
                break;
            case "down":
                if (bossRoomUnlocked) {
                    handleBossRoom();
                } else {
                    displayStory("The door to the boss room is locked. Solve all riddles to proceed.");
                }
                break;
        }
        this.revalidate();
        this.repaint();
    }

    private void handleRiddleRoom(String roomName, String roomDescription, String riddle, String answer, String hint) {
        if (solvedRiddles.contains(roomName)) {
            displayStory(roomDescription + " The riddle here has already been solved.");
            return;
        }

        displayStory(roomDescription + (hint.isEmpty() ? "" : " You found a hint: " + hint));
        solveButton.setVisible(true);

        // Remove previous listeners to prevent multiple executions
        for (ActionListener listener : solveButton.getActionListeners()) {
            solveButton.removeActionListener(listener);
        }

        solveButton.addActionListener(e -> {
            solveButton.setVisible(false); // Hide after solving
            String userAnswer = JOptionPane.showInputDialog(this, riddle);
            if (userAnswer != null && userAnswer.equalsIgnoreCase(answer)) {
                solvedRiddles.add(roomName);
                displayStory("Correct! You solved the riddle in the " + roomName + ".");
                if (solvedRiddles.size() == 3) {
                    bossRoomUnlocked = true;
                    displayStory("You hear a loud click as the boss room unlocks. You can now proceed down!");
                }
            } else {
                displayStory("Incorrect answer. Try again later or explore other rooms.");
            }
        });
    }

    private void handleBossRoom() {
        displayStory("You have entered the boss room, but you need to start the fight.");
        startFightButton.setVisible(true); // Show the start fight button when boss room is entered
        westLabel.setVisible(false);
        eastLabel.setVisible(false);
        northLabel.setVisible(false);

        this.revalidate();
        this.repaint();

        monsterLabel.setVisible(true);
        
    }

    

    private void startBossFight() {
        stopTempleMusic();
        playBossMusic();
        // Start the fight when the player presses the "Start Fight" button
        playerHealth = 100; // Reset player health
        bossHealth = 150;  // Set initial boss health
        displayStory("The battle begins! The boss stands before you.");
        westLabel.setVisible(false);
        eastLabel.setVisible(false);
        northLabel.setVisible(false);

        monsterLabel.setVisible(true);
        

        

        // Show attack and block buttons
        attackButton.setVisible(true);
        blockButton.setVisible(true);

        // Disable the start fight button
        startFightButton.setVisible(false);

       
    }



    private void attack() {
        // Show player's action immediately
        int damage = (int) (Math.random() * 30) + 10; // Random damage between 10 and 40
        bossHealth -= damage;
        displayStory("You attack the boss and deal " + damage + " damage! Boss health: " + bossHealth);

        playerBlocked = false;
    
        if (bossHealth <= 0) {
            displayStory("You have defeated the boss and escaped the temple! Congratulations!");
            endGame(true);
        } else {
            disableButtons(true);  // Disable buttons during boss's turn
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws InterruptedException {
                    Thread.sleep(3000);  // 3-second delay before boss's turn
                    return null;
                }
    
                @Override
                protected void done() {
                    bossTurn();  // Start the boss's turn after delay
                }
            }.execute();
        }
    }

    private void block() {
        displayStory("You block the boss's attack!");
        playerBlocked = true;
        disableButtons(true);  // Disable buttons during boss's turn
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws InterruptedException {
                Thread.sleep(3000);  // 3-second delay before boss's turn
                return null;
            }
    
            @Override
            protected void done() {
                bossTurn();  // Start the boss's turn after delay
            }
        }.execute();
    }
    private void disableButtons(boolean disable) {
        attackButton.setEnabled(!disable);
        blockButton.setEnabled(!disable);
    }

    private void bossTurn() {
        // Boss randomly chooses to attack or misses
        String action = Math.random() > 0.20 ? "attack" : "miss";

        if (playerBlocked) {
            displayStory("The boss got their attack blocked!" + " Player health: " + playerHealth);
        } else {
            if (action.equals("attack")) {
                int damage = (int) (Math.random() * 30) + 5;  // Random boss damage between 5 and 30
                playerHealth -= damage;
                displayStory("The boss attacks and deals " + damage + " damage! Your health: " + playerHealth);
                if (playerHealth <= 0) {
                    displayStory("You have been defeated by the boss.");
                    endGame(false);
                }
            } else {
                displayStory("The boss missed their attack!" +" Your health: " + playerHealth);
            }
        }
    
        // Enable buttons after the boss's turn
        disableButtons(false);
    }
    

    private void endGame(boolean won ) {
        String message = won ?"You have escaped the temple with your life! Congratulations you won! You will be rich beyond your wildest dreams due to your discovery! \nThanks for playing!" : "You have fallen in the temple. You have met the fate of many others before you. \nGame Over.";
        String message2 = won ? "You have slain the guardian of the temple!" : "You have been slain by the guardian of the temple.";
        displayStory(message2);
        stopBossMusic();
        playVictoryMusic();
        ImageIcon victoryIcon;
        if (won) {
        victoryIcon = new ImageIcon(getClass().getResource("victoryImage.jpg")); // Icon for winning
        } else {
        victoryIcon = new ImageIcon(getClass().getResource("defeat.jpg")); // Icon for losing
        }

        Image resizeImage = victoryIcon.getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH);
        ImageIcon sizedIcon = new ImageIcon(resizeImage);
        int option2 = JOptionPane.showOptionDialog(
            this,
            message,
            "Mysteries of the Secret Temple",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            sizedIcon,
            new String[]{"Replay?", "Quit Game"},
            "Enter"
        );

        if (option2 == JOptionPane.YES_OPTION) {
            initializeGame();
        } else {
            JOptionPane.showMessageDialog(this, "The game will now close. Goodbye!");
            System.exit(0);
        }
    
        
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        // Handle button actions
        if (e.getSource() == solveButton) {
            // Logic for solving the riddle
        } else if (e.getSource() == startFightButton) {
            startBossFight();
        } else if (e.getSource() == attackButton) {
            attack();
        } else if (e.getSource() == blockButton) {
            block();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        // Handle key inputs for navigation
        if (gameStarted) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP:
                    navigate("up");
                    break;
                case KeyEvent.VK_LEFT:
                    navigate("left");
                    break;
                case KeyEvent.VK_RIGHT:
                    navigate("right");
                    break;
                case KeyEvent.VK_DOWN:
                    navigate("down");
                    break;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main game = new Main();
            game.setVisible(true);
        });
    }
}
