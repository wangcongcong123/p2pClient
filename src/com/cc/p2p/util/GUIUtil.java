package com.cc.p2p.util;

/**
 * the class provides some static methods in order to manage the GUI better
 */
import java.awt.Component;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * this class is an util class
 * 
 * @author wangcongcong
 *
 */
public class GUIUtil {

	// the method enables a component to be the center of screen
	public static void toCenter(Component comp) {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		Rectangle rec = ge.getDefaultScreenDevice().getDefaultConfiguration().getBounds();
		comp.setLocation(((int) rec.getWidth() - comp.getWidth()) / 2,
				((int) rec.getHeight() - comp.getHeight()) / 2);
	}

	// the method is for setting the background of frames
	public static void SetBackground(JFrame jf, String fileName) {
		((JPanel) jf.getContentPane()).setOpaque(false);
		ImageIcon img = new ImageIcon(fileName);
		JLabel background = new JLabel(img);
		jf.getLayeredPane().add(background, new Integer(Integer.MIN_VALUE));
		background.setBounds(0, 0, img.getIconWidth(), img.getIconHeight());
	}

}
