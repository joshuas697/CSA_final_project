import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;

public class DAW extends JFrame {
    private Clip clip;
    private JLabel currentTrackLabel;
    private final int STEPS = 16;
    private final int TRACKS = 6;
    private JToggleButton[][] grid;
    private String[] trackNames = {
            "Kick",
            "Snare",
            "HiHat",
            "Clap",
            "Bass",
            "Synth"
    };
    private ArrayList<File> soundFiles = new ArrayList<>();
    private volatile boolean playing = false;
    private int bpm = 120;
    private ArrayList<Clip> clips = new ArrayList<>();

    public DAW() {
        setTitle("FL Studio");
        setSize(1000, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initializeSounds();
        initializeUI();
        setVisible(true);
    }

    private void initializeSounds() {
        String[] files = {
                "kick.wav",
                "snare.wav",
                "hihat.wav",
                "clap.wav",
                "bass.wav",
                "synth.wav"
        };

        try {
            for (String fileName : files) {
                AudioInputStream stream = AudioSystem.getAudioInputStream(new File("src/" + fileName));
                Clip clip = AudioSystem.getClip();
                clip.open(stream);
                clips.add(clip);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.setBackground(new Color(35, 35, 35));
        JButton playButton = new JButton("Play");
        JButton stopButton = new JButton("Stop");
        JButton loadButton = new JButton("Load Song");
        JLabel bpmLabel = new JLabel("BPM:");
        bpmLabel.setForeground(Color.WHITE);
        JSlider bpmSlider = new JSlider(60, 200, bpm);
        currentTrackLabel = new JLabel("No song loaded");
        currentTrackLabel.setForeground(Color.WHITE);
        playButton.addActionListener(e -> startSequencer());
        stopButton.addActionListener(e -> stopSequencer());
        loadButton.addActionListener(e -> loadAudioFile());
        bpmSlider.addChangeListener(e -> bpm = bpmSlider.getValue());
        topPanel.add(playButton);
        topPanel.add(stopButton);
        topPanel.add(loadButton);
        topPanel.add(bpmLabel);
        topPanel.add(bpmSlider);
        topPanel.add(currentTrackLabel);
        add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(TRACKS, STEPS));
        grid = new JToggleButton[TRACKS][STEPS];
        for (int row = 0; row < TRACKS; row++) {
            for (int col = 0; col < STEPS; col++) {
                JToggleButton button = new JToggleButton();
                button.setBackground(Color.DARK_GRAY);
                int finalRow = row;
                int finalCol = col;
                button.addItemListener(e -> {
                    if (button.isSelected()) {
                        button.setBackground(Color.ORANGE);
                    } else {
                        button.setBackground(Color.DARK_GRAY);
                    }
                });
                grid[row][col] = button;
                centerPanel.add(button);
            }
        }
        add(centerPanel, BorderLayout.CENTER);

        JPanel leftPanel = new JPanel(new GridLayout(TRACKS, 1));
        for (String name : trackNames) {
            JLabel label = new JLabel(name, SwingConstants.CENTER);
            label.setOpaque(true);
            label.setBackground(new Color(50, 50, 50));
            label.setForeground(Color.WHITE);
            leftPanel.add(label);
        }
        add(leftPanel, BorderLayout.WEST);

        JPanel bottomPanel = new JPanel();
        JLabel info = new JLabel(
                "FL Studio"
        );
        bottomPanel.add(info);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadAudioFile() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                File file = chooser.getSelectedFile();
                AudioInputStream audioStream =
                        AudioSystem.getAudioInputStream(file);
                clip = AudioSystem.getClip();
                clip.open(audioStream);
                currentTrackLabel.setText(file.getName());
                clip.start();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Error loading audio file."
                );
                ex.printStackTrace();
            }
        }
    }

    private void startSequencer() {
        if (playing) return;
        playing = true;
        Thread sequencerThread = new Thread(() -> {
            int step = 0;
            while (playing) {
                long delay = 60000 / bpm / 4;
                highlightStep(step);
                for (int track = 0; track < TRACKS; track++) {
                    if (grid[track][step].isSelected()) {
                        playSound(track);
                    }
                }
                step++;
                if (step >= STEPS) {
                    step = 0;
                }
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            clearHighlights();
        });
        sequencerThread.start();
    }

    private void stopSequencer() {
        playing = false;
    }

    private void playSound(int track) {
        try {
            Clip clip = clips.get(track);
            if (clip.isRunning()) {
                clip.stop();
            }
            clip.setFramePosition(0);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void highlightStep(int currentStep) {
        SwingUtilities.invokeLater(() -> {
            clearHighlights();
            for (int row = 0; row < TRACKS; row++) {
                if (grid[row][currentStep].isSelected()) {
                    grid[row][currentStep].setBackground(Color.GREEN);
                } else {
                    grid[row][currentStep].setBackground(Color.GRAY);
                }
            }
        });
    }

    private void clearHighlights() {
        SwingUtilities.invokeLater(() -> {
            for (int row = 0; row < TRACKS; row++) {
                for (int col = 0; col < STEPS; col++) {
                    if (grid[row][col].isSelected()) {
                        grid[row][col].setBackground(Color.ORANGE);
                    } else {
                        grid[row][col].setBackground(Color.DARK_GRAY);
                    }
                }
            }
        });
    }
}
