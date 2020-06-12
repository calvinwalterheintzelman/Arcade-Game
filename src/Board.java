import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.*;
import javax.sound.sampled.*;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.io.*;


public class Board extends JPanel implements ActionListener {
    private final int B_WIDTH = 300;
    private final int B_HEIGHT = 420;
    private final int DOT_SIZE = 10;
    private final int ALL_DOTS = 1350;
    private final int RAND_POS_X = 27;
    private final int RAND_POS_Y = 32;
    private final int DELAY = 50;
    private int lives = 3;
    private boolean reset_cent = false;

    private final int total_dots = 10;
    private final int x[][] = new int[total_dots][ALL_DOTS];
    private final int y[][] = new int[total_dots][ALL_DOTS];

    private int c_health[][] = new int[total_dots][total_dots];

    private final int apps = 80;
    private int dots[] = new int[total_dots];
    private int apple_x[] = new int[apps];
    private int apple_y[] = new int[apps];
    private int apple_health[] = new int[apps];
    private int spider_health = 2;
    private int total_cents = 1;

    private boolean leftDirection[] = {true, true, true, true, true, true, true, true, true, true};
    private boolean rightDirection[] = {false, false, false, false, false, false, false, false, false, false};
    private boolean downDirection[] = {false, false, false, false, false, false, false, false, false, false};
    private boolean inGame = true;
    private boolean right[] = {false, false, false, false, false, false, false, false, false, false};

    private long score = 0;

    private int shoot_x = 300;
    private int shoot_y = -10;

    private int p_x = 150;
    private int p_y = 380;
    private int prev_x = 0;
    private int prev_y = 0;
    private int spider_x = 0;
    private int spider_y = 340;

    private Timer timer;
    private Image ball;
    private Image apple;
    private Image head;
    private Image spider;
    private Image shooter;
    private Image laser;
    private Image bad_apple;
    private Image rotten_apple;
    private Image hurt_spider;
    private Image dot_hurt;
    private Image head_hurt;

    public Board() {

        initBoard();
    }

    private void initBoard() {

        addKeyListener(new TAdapter());
        addMouseMotionListener(new MAdapter());
        addMouseListener(new MAdapter());
        setBackground(Color.black);
        setFocusable(true);

        setPreferredSize(new Dimension(B_WIDTH, B_HEIGHT));
        loadImages();
        initApps();
        initCent();
        initGame();
    }

    private void initCent() {
        for(int n = 0; n < total_dots; n++){
            for(int m = 0; m < total_dots; m++){
                c_health[n][m] = 2;
            }
        }
    }

    private void initApps() {
        for(int i = 0; i < apps; i++){
            apple_health[i] = 3;
        }
    }

    private void loadImages() {

        ImageIcon hdid = new ImageIcon("dot_hurt.png");
        dot_hurt = hdid.getImage();

        ImageIcon hhid = new ImageIcon("head_hurt.png");
        head_hurt = hhid.getImage();

        ImageIcon hsid = new ImageIcon("spider_hurt.png");
        hurt_spider = hsid.getImage();

        ImageIcon baid = new ImageIcon("bad_apple.png");
        bad_apple = baid.getImage();

        ImageIcon raid = new ImageIcon("rotten_apple.png");
        rotten_apple = raid.getImage();

        ImageIcon lid = new ImageIcon("laser.png");
        laser = lid.getImage();

        ImageIcon shid = new ImageIcon("shooter.png");
        shooter = shid.getImage();

        ImageIcon sid = new ImageIcon("spider.png");
        spider = sid.getImage();

        ImageIcon iid = new ImageIcon("dot.png");
        ball = iid.getImage();

        ImageIcon iia = new ImageIcon("apple.png");
        apple = iia.getImage();

        ImageIcon iih = new ImageIcon("head.png");
        head = iih.getImage();
    }

    private void initGame() {

        for(int k = 0; k < total_dots; k++) {
            if(k == 0) {
                dots[k] = total_dots;
            }
            else {
                dots[k] = 0;
            }

            for (int z = 0; z < dots[k]; z++) {
                x[k][z] = 290 + z * 10;
                y[k][z] = 0;
            }
        }


        locateApple();

        timer = new Timer(DELAY, this);
        timer.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        doDrawing(g);
    }

    private void doDrawing(Graphics g) {

        if (inGame) {
            for(int i = 0; i < apps; i++) {
                if(apple_health[i] == 3) {
                    g.drawImage(apple, apple_x[i], apple_y[i], this);
                }
                else if(apple_health[i] == 2) {
                    g.drawImage(bad_apple, apple_x[i], apple_y[i], this);
                }
                else if(apple_health[i] == 1) {
                    g.drawImage(rotten_apple, apple_x[i], apple_y[i], this);
                }
            }

            for(int k = 0; k < total_dots; k++) {
                for (int z = 0; z < dots[k]; z++) {
                    if (z == 0) {
                        if(c_health[k][z] == 2) {
                            g.drawImage(head, x[k][z], y[k][z], this);
                        }
                        else if(c_health[k][z] == 1) {
                            g.drawImage(head_hurt, x[k][z], y[k][z], this);
                        }
                    } else {
                        if(c_health[k][z] == 2) {
                            g.drawImage(ball, x[k][z], y[k][z], this);
                        }
                        else if (c_health[k][z] == 1) {
                            g.drawImage(dot_hurt, x[k][z], y[k][z], this);
                        }
                    }
                }
            }

            g.drawImage(shooter, p_x, p_y, this);
            g.drawImage(laser, shoot_x, shoot_y, this);

            if(spider_health == 2) {
                g.drawImage(spider, spider_x, spider_y, this);
            }
            else if(spider_health == 1) {
                g.drawImage(hurt_spider, spider_x, spider_y, this);
            }

            String msg = "Score: " + score;
            Font small = new Font("Helvetica", Font.BOLD, 16);

            g.setColor(Color.white);
            g.setFont(small);
            g.drawString(msg, 5, 418);

            String message = "Lives: " + lives;
            Font bot = new Font("Helvetica", Font.BOLD, 16);

            g.setColor(Color.white);
            g.setFont(bot);
            g.drawString(message, 235, 418);

            Toolkit.getDefaultToolkit().sync();

        } else {

            gameOver(g);
        }
    }

    private void gameOver(Graphics g) {

        String msg = "Game Over";
        Font small = new Font("Helvetica", Font.BOLD, 14);
        FontMetrics metr = getFontMetrics(small);

        g.setColor(Color.white);
        g.setFont(small);
        g.drawString(msg, (B_WIDTH - metr.stringWidth(msg)) / 2, B_HEIGHT / 2);
    }

    private void move() {
        for(int k = 0; k < total_dots; k++) {
            for (int z = dots[k]; z > 0; z--) {
                x[k][z] = x[k][(z - 1)];
                y[k][z] = y[k][(z - 1)];
            }
            if (leftDirection[k]) {
                x[k][0] -= DOT_SIZE;
            }
            if (rightDirection[k]) {
                x[k][0] += DOT_SIZE;
            }
            if (downDirection[k]) {
                y[k][0] += DOT_SIZE;
            }
        }
    }

    private void resetCent() {
        initCent();
        for(int k = 0; k < total_dots; k++) {
            if(k == 0) {
                dots[k] = total_dots;
            }
            else {
                dots[k] = 0;
            }

            for (int z = 0; z < dots[k]; z++) {
                x[k][z] = 290 + z * 10;
                y[k][z] = 0;
            }
        }
        for(int n = 0; n < total_dots; n++) {
            leftDirection[n] = true;
            rightDirection[n] = false;
            downDirection[n] = false;
            right[n] = false;
        }
    }

    private void resetBoard() {
        for(int i = 0; i < apps; i++) {
            if(apple_health[i] == 1 || apple_health[i] == 2) {
                apple_health[i] = 3;
                score += 10;
            }
        }
        p_x = 150;
        p_y = 380;
        spider_health = 2;
        spider_x = 0;
        spider_y = 340;
        total_cents = 1;
        initCent();
        for(int k = 0; k < total_dots; k++) {
            if(k == 0) {
                dots[k] = total_dots;
            }
            else {
                dots[k] = 0;
            }

            for (int z = 0; z < dots[k]; z++) {
                x[k][z] = 290 + z * 10;
                y[k][z] = 0;
            }
        }
        shoot_x = 300;
        shoot_y = -10;
        for(int n = 0; n < total_dots; n++) {
            leftDirection[n] = true;
            rightDirection[n] = false;
            downDirection[n] = false;
            right[n] = false;
        }
    }

    private void checkCollision() {

        if(p_x == spider_x && p_y == spider_y) {
            lives -= 1;
            resetBoard();
        }
        for(int k = 0; k < total_cents; k ++) {
            for(int z = 0; z < dots[k]; z++) {
                if(p_x == x[k][z] && p_y == y[k][z]) {
                    lives -= 1;
                    resetBoard();
                }
            }
        }

        if(lives == 0) {
            inGame = false;
        }

        if(Math.random() < 0.5) { //move only 1 direction
            double check = Math.random();
            if(spider_y == 390) {
                spider_y -= 10;
            }
            else if(spider_y == 0) {
                spider_y += 10;
            }
            else if(spider_x == 290) {
                spider_x -= 10;
            }
            else if(spider_x == 0) {
                spider_x += 10;
            }
            else if(check < 0.25) {
                spider_y -= 10;
            }
            else if(check < 0.5 ){
                spider_y += 10;
            }
            else if(check < 0.75){
                spider_x -= 10;
            }
            else {
                spider_x += 10;
            }
        }
        else { //move 2 directions
            double check = Math.random();
            if(spider_x == 0) {
                spider_x += 10;
                if(spider_y == 0) {
                    spider_y += 10;
                }
                else if (spider_y == 390){
                    spider_y -= 10;
                }
                else if(check < 0.5) {
                    spider_y += 10;
                }
                else {
                    spider_y -= 10;
                }
            }
            else if(spider_x == 290) {
                spider_x -= 10;
                if(spider_y == 0) {
                    spider_y += 10;
                }
                else if (spider_y == 390){
                    spider_y -= 10;
                }
                else if(check < 0.5) {
                    spider_y += 10;
                }
                else {
                    spider_y -= 10;
                }
            }
            else if(spider_y == 390) {
                spider_y -= 10;
                if(check < 0.5) {
                    spider_x += 10;
                }
                else {
                    spider_x -= 10;
                }
            }
            else if(spider_y == 0) {
                spider_y += 10;
                if(check < 0.5) {
                    spider_x += 10;
                }
                else {
                    spider_x -= 10;
                }
            }
            else{
                if(check < 0.25) {
                    spider_x += 10;
                    spider_y += 10;
                }
                else if(check < 0.5) {
                    spider_x += 10;
                    spider_y -= 10;
                }
                else if(check < 0.75) {
                    spider_x -= 10;
                    spider_y += 10;
                }
                else {
                    spider_x -= 10;
                    spider_y -= 10;
                }
            }
        }

        for(int k = 0; k < total_dots; k++) {
            for (int z = 0; z < dots[k]; z++) {
                if(shoot_x == x[k][z] && shoot_y == y[k][z]) {
                    c_health[k][z] -= 1;
                    shoot_y = -10;
                    score += 2;
                    if(c_health[k][z] == 0) {
                        score += 3;
                        if(z == dots[k] - 1) {
                            x[k][z] = -1000000;
                            y[k][z] = -1000000;
                            dots[k] -= 1;
                            if(dots[k] == 0) {
                                score += 595;
                            }
                        }
                        else if(z == 0) {
                            c_health[k][z] = 2;
                            dots[k] -= 1;
                            x[k][dots[k]] = -1000000;
                            y[k][dots[k]] = -1000000;
                            if(dots[k] == 0) {
                                score += 595;
                            }
                            break;
                        }
                        else {
                            dots[total_cents] = dots[k] - z - 1;
                            for(int m = z + 1; m < dots[k]; m++){
                                x[total_cents][m - z - 1] = x[k][m];
                                y[total_cents][m - z - 1] = y[k][m];
                                x[k][m] = -1000000;
                                y[k][m] = -1000000;
                            }
                            dots[k] = dots[k] - dots[total_cents] - 1;
                            total_cents += 1;
                        }
                    }
                }
            }
        }

        if(shoot_x == spider_x && shoot_y == spider_y){
            spider_health -= 1;
            shoot_y = -10;
            score += 100;
            if(spider_health == 0) {
                spider_x = 310;
                spider_y = 410;
                score += 500;
            }
        }

        if(shoot_x != 300) {
            if(shoot_y >= 0){
                shoot_y -= 10;
            }
        }

        for(int k = 0; k < total_dots; k++) {
            if (downDirection[k] && right[k] && x[k][0] + 10 < B_WIDTH && x[k][0] - 10 >= 0) {
                downDirection[k] = false;
                right[k] = false;
                leftDirection[k] = true;
            }

            if (downDirection[k] && !right[k] && x[k][0] + 10 < B_WIDTH && x[k][0] - 10 >= 0) {
                downDirection[k] = false;
                right[k] = true;
                rightDirection[k] = true;
            }

            if (y[k][0] != 340) {
                for (int i = 0; i < apps; i++) {
                    if (rightDirection[k] && x[k][0] + 10 == apple_x[i] && y[k][0] == apple_y[i]) {
                        rightDirection[k] = false;
                        downDirection[k] = true;
                        break;
                    } else if (leftDirection[k] && x[k][0] - 10 == apple_x[i] && y[k][0] == apple_y[i]) {
                        leftDirection[k] = false;
                        downDirection[k] = true;
                        break;
                    }
                }
            }

            if (x[k][0] + 10 >= B_WIDTH && downDirection[k]) {
                leftDirection[k] = true;
                rightDirection[k] = false;
                downDirection[k] = false;
            } else if (x[k][0] + 10 >= B_WIDTH && rightDirection[k] && y[k][0] != 340) {
                leftDirection[k] = false;
                rightDirection[k] = false;
                downDirection[k] = true;
            }
            if (x[k][0] - 10 < 0 && downDirection[k]) {
                leftDirection[k] = false;
                rightDirection[k] = true;
                downDirection[k] = false;
            } else if (x[k][0] - 10 < 0 && y[k][0] != 340) {
                leftDirection[k] = false;
                rightDirection[k] = false;
                downDirection[k] = true;
            }
            if (y[k][0] == 340 && x[k][0] < 10) {
                rightDirection[k] = true;
                leftDirection[k] = false;
                downDirection[k] = false;
            } else if (y[k][0] == 340 && x[k][0] + 10 >= B_WIDTH) {
                rightDirection[k] = false;
                leftDirection[k] = true;
                downDirection[k] = false;
            }
        }

        for(int i = 0; i < apps; i++){
            if(shoot_x == apple_x[i] && shoot_y == apple_y[i]){
                apple_health[i] -= 1;
                shoot_y = -10;
                score += 1;
                if(apple_health[i] == 0){
                    apple_x[i] = 310;
                    apple_y[i] = 410;
                    score += 4;
                }
            }
        }

        reset_cent = true;
        for(int i = 0; i < total_cents; i++) {
            if(dots[i] != 0) {
                reset_cent = false;
                break;
            }
        }

        if(reset_cent) {
            resetCent();
        }

        if (!inGame) {
            timer.stop();
        }
    }

    private void locateApple() {
        for (int i = 0; i < apps; i++) {
            int r = (int) (Math.random() * RAND_POS_X + 1);
            apple_x[i] = ((r * DOT_SIZE));

            r = (int) (Math.random() * RAND_POS_Y + 1);
            apple_y[i] = ((r * DOT_SIZE));
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (inGame) {

            checkCollision();
            move();
        }

        repaint();
    }

    private class MAdapter extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            if(shoot_y == -10) {
                shoot_x = p_x;
                shoot_y = p_y;
                try {
                    File audioFile = new File("pew.wav");
                    AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
                    AudioFormat format = audioStream.getFormat();
                    DataLine.Info info = new DataLine.Info(Clip.class, format);
                    Clip audioCLip = (Clip) AudioSystem.getLine(info);
                    audioCLip.open(audioStream);
                    audioCLip.start();
                }
                catch (Exception Ex) {
                    System.out.println(Ex);
                }

            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            java.awt.Point loc = e.getLocationOnScreen();
            int x_dif = loc.x - prev_x;
            int y_dif = loc.y - prev_y;
            int total_dif_x = java.lang.Math.abs(x_dif);
            int total_dif_y = java.lang.Math.abs(y_dif);

            if(total_dif_x >= total_dif_y && x_dif > 0 && p_x < 290) {
                p_x += 10;
            }
            else if(total_dif_x >= total_dif_y && x_dif < 0 && p_x > 0) {
                p_x -= 10;
            }
            else if(total_dif_x < total_dif_y && y_dif > 0 && p_y < 390) {
                p_y += 10;
            }
            else if(total_dif_x < total_dif_y && y_dif < 0 && p_y > 340) {
                p_y -= 10;
            }

            prev_x = loc.x;
            prev_y = loc.y;

        }
    }

    private class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {

            int key = e.getKeyCode();

            if (key == KeyEvent.VK_SPACE) {
                if(shoot_y == -10) {
                    shoot_x = p_x;
                    shoot_y = p_y;
                    try {
                        File audioFile = new File("pew.wav");
                        AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
                        AudioFormat format = audioStream.getFormat();
                        DataLine.Info info = new DataLine.Info(Clip.class, format);
                        Clip audioCLip = (Clip) AudioSystem.getLine(info);
                        audioCLip.open(audioStream);
                        audioCLip.start();
                    }
                    catch (Exception Ex) {
                        System.out.println(Ex);
                    }

                }

            }

            if (key == KeyEvent.VK_LEFT) {
                if(p_x > 0) {
                    p_x -= 10;
                }
            }

            if (key == KeyEvent.VK_RIGHT) {
                if(p_x < 290) {
                    p_x += 10;
                }
            }

            if (key == KeyEvent.VK_UP) {
                if(p_y > 340) {
                    p_y -= 10;
                }
            }

            if (key == KeyEvent.VK_DOWN) {
                if(p_y < 390) {
                    p_y += 10;
                }
            }
        }
    }
}
