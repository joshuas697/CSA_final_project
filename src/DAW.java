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
    private final int TRACKS = 4;
    private JToggleButton[][] grid;
    private String[] trackNames = {
            "Kick",
            "Snare",
            "HiHat",
            "Clap"
    };
    private ArrayList<File> soundFiles = new ArrayList<>();
    private volatile boolean playing = false;
    private int bpm = 120;
}
