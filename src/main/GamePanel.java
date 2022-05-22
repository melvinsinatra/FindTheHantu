package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

    Random rand;
    static final int SCREEN_WIDTH = 525;
    static final int SCREEN_HEIGHT = 525;
    static final int UNITS_SIZE = 25;
    static final int DELAY = 175;

    int playerX;
    int playerY;
    char playerDirection; //R = Right, L = Left, D = Down, U = Up
    char ghostDirection;
    char arrayOfDirections[]  = new char[4];
    int ghostCount;
    int ghostX[] = new int[5];
    int ghostY[] = new int[5];
    int obstacleX[] = new int[29];
    int obstacleY[] = new int[29];
    int ghostsEliminatedCount;
    boolean running = false;
    boolean ghostsTurn;
    Timer timer;

    public GamePanel() {
        rand = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();
    }

    public void startGame() {
        ghostCount = 5;
        addGhost();
        addObstacles();
        running = true;
        ghostsTurn = true;
        timer = new Timer(DELAY, this);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        if(running) {
            //Grid
//            for(int i = 0; i<SCREEN_HEIGHT/UNITS_SIZE; i++) {
//                g.setColor(Color.gray);
//                g.drawLine(i*UNITS_SIZE, 0, i*UNITS_SIZE, SCREEN_HEIGHT);
//                g.drawLine(0, i*UNITS_SIZE, SCREEN_WIDTH, i*UNITS_SIZE);
//            }

            //Obstacles
            g.setColor(Color.gray);
            for (int i=0; i<29; i++) {
                g.fillRect(obstacleX[i], obstacleY[i], UNITS_SIZE, UNITS_SIZE);
            }

            //Ghost
            for (int i=0; i<5; i++) {
                g.setColor(Color.white);
                if (ghostX[i] == -1000 && ghostY[i] == -1000) {
                    g.fillOval(ghostX[i], ghostY[i], 0, 0);
                } else {
                    g.setColor(Color.white);
                    g.fillOval(ghostX[i], ghostY[i], UNITS_SIZE, UNITS_SIZE);
                }
            }

            //Player
            g.setColor(Color.cyan);
            g.fillRect(playerX, playerY, UNITS_SIZE, UNITS_SIZE);

            //Ghosts Left
            g.setColor(Color.red);
            g.setFont( new Font("Calibri",Font.BOLD, 24));
            FontMetrics metrics = getFontMetrics(g.getFont());
            String message = "Ghosts Left: " + ghostCount;
            g.drawString(message, (SCREEN_WIDTH - metrics.stringWidth(message))/2, g.getFont().getSize());
        }

        else if (!running) {
            if(ghostCount >= 1) {
                gameOver(g);
            } else if (ghostCount == 0) {
                youWon(g);
            }
        }
    }

    public void addGhost() {
        for (int i=0; i<5; i++) {
            ghostX[i] = rand.nextInt(SCREEN_WIDTH/UNITS_SIZE)*UNITS_SIZE;
            ghostY[i] = rand.nextInt(SCREEN_HEIGHT/UNITS_SIZE)*UNITS_SIZE;
        }

    }

    public void addObstacles() {
        for(int i=0; i<29; i++) {
            obstacleX[i] = rand.nextInt(SCREEN_WIDTH/UNITS_SIZE)*UNITS_SIZE;
            obstacleY[i] = rand.nextInt(SCREEN_WIDTH/UNITS_SIZE)*UNITS_SIZE;
        }
    }

    public void movePlayer() {
        switch (playerDirection) {
            case 'U':
                playerY = playerY - UNITS_SIZE;
                break;
            case 'D':
                playerY = playerY + UNITS_SIZE;
                break;
            case 'L':
                playerX = playerX - UNITS_SIZE;
                break;
            case 'R':
                playerX = playerX + UNITS_SIZE;
                break;
        }
    }

    public void moveGhosts() {
        arrayOfDirections[0] = 'R';
        arrayOfDirections[1] = 'L';
        arrayOfDirections[2] = 'D';
        arrayOfDirections[3] = 'U';

        for (int i=0; i<5; i++) {
            int randNum = rand.nextInt(4);
            ghostDirection = arrayOfDirections[randNum];

            if(ghostX[i] != -1000 && ghostY[i] != -1000) {
                switch (ghostDirection) {

                    case 'U':
                        ghostY[i] = ghostY[i] - UNITS_SIZE;
                        if(ghostY[i] <= 0) { //If Ghost touches TOP border, move DOWN
                            ghostY[i] = ghostY[i] + (2*UNITS_SIZE);
                        }
                        break;

                    case 'D':
                        ghostY[i] = ghostY[i] + UNITS_SIZE;
                        if(ghostY[i] >= SCREEN_HEIGHT) { //If Ghost touches BOTTOM border, move UP
                            ghostY[i] = ghostY[i] - (2*UNITS_SIZE);
                        }
                        break;

                    case 'L':
                        ghostX[i] = ghostX[i] - UNITS_SIZE;
                        if(ghostX[i] <= 0) { //If Ghost touches LEFT border, move RIGHT
                            ghostX[i] = ghostX[i] + (2*UNITS_SIZE);
                        }
                        break;

                    case 'R':
                        ghostX[i] = ghostX[i] + UNITS_SIZE;
                        if(ghostX[i] >= SCREEN_WIDTH) { //If Ghost touches RIGHT border, move LEFT
                            ghostX[i] = ghostX[i] - (2*UNITS_SIZE);
                        }
                        break;
                }
            }
        }
    }

    public void eliminateGhost(){

        for (int i=0; i<5; i++) {
            if(playerX == ghostX[i] && playerY == ghostY[i]) {
                ghostsEliminatedCount++;
                ghostX[i] = -1000;
                ghostY[i] = -1000;
                ghostCount--;
            }

        }

        if(ghostCount==0) {
            running = false;
        }

    }

    public void checkCollisions() {
        //Check if touching screen border
        if (playerX < 0 || playerX == SCREEN_WIDTH || playerY < 0 || playerY == SCREEN_HEIGHT) {
            running = false;
        }
        //Check if touching obstacles
        for(int i=0; i<29; i++) {
            if(playerX == obstacleX[i] && playerY == obstacleY[i]) {
                running = false;
            }
        }

        if(!running) {
            timer.stop();
        }
    }

    public void gameOver(Graphics g) {
        //Game Over
        g.setColor(Color.red);
        g.setFont(new Font("Calibri", Font.BOLD, 64));
        FontMetrics metrics = getFontMetrics(g.getFont());
        String gameOver = "Game Over";
        g.drawString(gameOver, (SCREEN_WIDTH - metrics.stringWidth(gameOver))/2, SCREEN_HEIGHT/2);
    }

    public void youWon(Graphics g) {
        g.setColor(Color.green);
        g.setFont(new Font("Calibri", Font.BOLD, 24));
        FontMetrics metrics = getFontMetrics(g.getFont());
        String message = "You Won! Thanks for playing the game o(•ω•`)o";
        g.drawString(message, (SCREEN_WIDTH - metrics.stringWidth(message))/2, SCREEN_HEIGHT/2);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if(running) {
            if(ghostsTurn) {
                moveGhosts();
                ghostsTurn = false;
            } else if (!ghostsTurn) {
                ghostsTurn = true;
            }
            movePlayer();
            eliminateGhost();
            checkCollisions();
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            timer.start();
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    playerDirection = 'L';
                    break;
                case KeyEvent.VK_RIGHT:
                    playerDirection = 'R';
                    break;
                case KeyEvent.VK_UP:
                    playerDirection = 'U';
                    break;
                case KeyEvent.VK_DOWN:
                    playerDirection = 'D';
                    break;
            }
        }
    }
}
