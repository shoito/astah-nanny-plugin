package com.github.astah.nanny;


import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.astah.nanny.updater.AutoUpdater;
import com.github.astah.nanny.util.ConfigurationUtils;

public class Activator implements BundleActivator {
	private static final Logger logger = LoggerFactory.getLogger(Activator.class);
	private static final long DELAY = 4 * 60 * 60 * 1000;
	private static final long INTERVAL = 1 * 60 * 60 * 1000;

	private AstahAPIHandler handler = new AstahAPIHandler();

	public void start(BundleContext context) {
		Map<String, String> config = ConfigurationUtils.load();
		String updateCheckStr = config.get(ConfigurationUtils.UPDATE_CHECK);
		logger.info("Are there newer versions available? " + updateCheckStr);
		
		if ("false".equalsIgnoreCase(updateCheckStr)) {
			return;
		}
		
		startWork();
		runAutoUpdater();
	}

	public void stop(BundleContext context) {
	}
	
	private void startWork() {
		Runnable task = new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(DELAY);
				} catch (InterruptedException e) {
					logger.warn(e.getMessage(), e);
				}
				
				doNanny();
			}
		};
		Executor executor = Executors.newSingleThreadExecutor();
		executor.execute(task);
	}
	
	private void doNanny() {
		Runnable task = new Runnable() {
			@Override
			public void run() {
				sayFinish();
				int selectedOption = JOptionPane.showConfirmDialog(
						handler.getMainFrame(), Messages.getMessage("message.take_a_break"), "Astah Nanny",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE,
						new ImageIcon(this.getClass().getResource("claudia.png"))
						);
				if (selectedOption == JOptionPane.YES_OPTION) {
					JFrame mainFrame = handler.getMainFrame();
					mainFrame.dispatchEvent(new WindowEvent(mainFrame,
							WindowEvent.WINDOW_CLOSING));
				}
				
				try {
					Thread.sleep(INTERVAL);
				} catch (InterruptedException e) {
					logger.warn(e.getMessage(), e);
				}
				
				doNanny();
			}
		};
		Executor executor = Executors.newSingleThreadExecutor();
		executor.execute(task);
	}
	
	private void sayFinish() {
		AudioFormat format = null;
		DataLine.Info info = null;
		Clip line = null;

		try {
			URL resource = this.getClass().getResource("03syuuryou.wav");
			format = AudioSystem.getAudioFileFormat(resource).getFormat();
			info = new DataLine.Info(Clip.class, format);
			line = (Clip) AudioSystem.getLine(info);
			line.open(AudioSystem.getAudioInputStream(resource));
			line.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void runAutoUpdater() {
		Runnable task = new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(358000);
				} catch (InterruptedException e) {
					logger.warn(e.getMessage(), e);
				}
				
				AutoUpdater autoUpdater = new AutoUpdater();
				try {
					autoUpdater.check();
				} catch (IOException e) {
					logger.warn(e.getMessage(), e);
				}
			}
		};
		Executor executor = Executors.newSingleThreadExecutor();
		executor.execute(task);
	}
}
