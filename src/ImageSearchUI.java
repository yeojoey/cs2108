import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

public class ImageSearchUI extends JFrame {
	
	private ImageSearch is;
	private QueryProcessor qp;
	private List<SearchType> types;
	
	private JPanel contentPane;
	private JPanel resultsPanel;
	private JPanel sidePanel;
	private JPanel queryImagePanel;
	private JPanel featuresOptionPanel;
	
	private JLabel queryImageLabel;
	private JLabel[] resultsLabelArray;
	private JLabel detailsLabel;
	private JLabel f1Label;
	
	private JButton selectImageBtn;
	private JButton searchBtn;
	
	private JCheckBox colorCheckBox;
	private JCheckBox textCheckBox;
	private JCheckBox semanticCheckBox;
	
	private ImageIcon imageIcon;
	
	private JScrollPane scrollPane;
	
	private ItemListener checkBoxItemListener;
	private MouseListener imageClickedListener;
	
	private int selectedFeatures = 0;
	private String queryImagePath;
	private List<ImageData> results;
	
	public ImageSearchUI() {
		is = new ImageSearch();
		qp = new QueryProcessor(is);
		types = new ArrayList<SearchType>();
		
		contentPane = (JPanel) this.getContentPane();
		resultsPanel = new JPanel();
		sidePanel = new JPanel();
		queryImagePanel = new JPanel();
		featuresOptionPanel = new JPanel();
		
		queryImageLabel = new JLabel("No image selected", SwingConstants.CENTER);
		resultsLabelArray = new JLabel[16];
		f1Label = new JLabel("<html>Precision: <br>Recall: <br>F1: </html>");
		detailsLabel = new JLabel("<html>Select a result to<br>view more details</html>", SwingConstants.CENTER);
		scrollPane = new JScrollPane(detailsLabel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		selectImageBtn = new JButton("Select image");
		searchBtn = new JButton("Search");
		
		colorCheckBox = new JCheckBox("Color histogram");
		textCheckBox = new JCheckBox("Text");
		semanticCheckBox = new JCheckBox("Semantic feature");
		
		checkBoxItemListener = new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				Object item = e.getItemSelectable();
				
				// updates number of features to search with
				if (e.getStateChange() == ItemEvent.DESELECTED) {
					if (item == colorCheckBox) {
						types.remove(SearchType.COLORHIST);
						selectedFeatures--;
					} else if (item == textCheckBox) {
						types.remove(SearchType.TEXT);
						selectedFeatures--;
					} else if (item == semanticCheckBox) {
						types.remove(SearchType.SEMANTIC);
						selectedFeatures--;
					}
					
					if (selectedFeatures == 0) {
						searchBtn.setEnabled(false);
					}
				} else {
					if (item == colorCheckBox) {
						types.add(SearchType.COLORHIST);
						selectedFeatures++;
						searchBtn.setEnabled(true);
					} else if (item == textCheckBox) {
						types.add(SearchType.TEXT);
						selectedFeatures++;
						searchBtn.setEnabled(true);
					} else if (item == semanticCheckBox) {
						types.add(SearchType.SEMANTIC);
						selectedFeatures++;
						searchBtn.setEnabled(true);
					}
				}
				
			}
		};
		
		imageClickedListener = new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (results != null) {
					JLabel imageLabel = (JLabel) e.getSource();
					String labelText = imageLabel.getText();
					int index = Integer.parseInt(labelText.substring(6, labelText.length())) - 1;
					detailsLabel.setText(results.get(index).toLabelText());
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {}

			@Override
			public void mouseReleased(MouseEvent e) {}

			@Override
			public void mouseEntered(MouseEvent e) {}

			@Override
			public void mouseExited(MouseEvent e) {}
		};
		
		setTitle("Image Search System");
		setSize(900, 700);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		init();
	}
	
	private void init() {
		// query image panel
		queryImageLabel.setPreferredSize(new Dimension(150, 150));
		queryImageLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		queryImageLabel.setForeground(Color.LIGHT_GRAY);
		
		queryImagePanel.setPreferredSize(new Dimension(200, 200));
		queryImagePanel.add(queryImageLabel);
		queryImagePanel.add(selectImageBtn);
		
		selectImageBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc = new JFileChooser();
				jfc.setCurrentDirectory(new File(".."));
				jfc.setDialogTitle("Select a new query image");
				int returnVal = jfc.showOpenDialog(ImageSearchUI.this);
				
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					queryImagePath = jfc.getSelectedFile().getAbsolutePath();
					imageIcon = new ImageIcon(queryImagePath);
					queryImageLabel.setIcon(imageIcon);
				}
			}
		});
		
		// select features panel
		colorCheckBox.addItemListener(checkBoxItemListener);
		textCheckBox.addItemListener(checkBoxItemListener);
		semanticCheckBox.addItemListener(checkBoxItemListener);
		
		featuresOptionPanel.setLayout(new BoxLayout(featuresOptionPanel, BoxLayout.Y_AXIS));
		featuresOptionPanel.setBorder(BorderFactory.createTitledBorder("Select features:"));
		featuresOptionPanel.setPreferredSize(new Dimension(150, 100));
		featuresOptionPanel.add(colorCheckBox);
		featuresOptionPanel.add(textCheckBox);
		featuresOptionPanel.add(semanticCheckBox);
		
		searchBtn.setEnabled(false);
		searchBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!queryImagePath.isEmpty()) {
					try {
						File img = new File(queryImagePath);
						results = qp.processQuery(types, img);
						detailsLabel.setText(results.get(0).toLabelText());
						double[] f1Results = qp.getF1();
						f1Label.setText(String.format("<html>Precision: %f<br>Recall: %f<br>F1: %f</html>",
								f1Results[0], f1Results[1], f1Results[2]));
						for (int i = 0; i < 16; i++) {
							resultsLabelArray[i].setIcon(new ImageIcon(results.get(i).getFilepath()));
						}
						
						repaint();
					} catch (IOException ioe) {
						ioe.printStackTrace();
					}
				}
			}
		});
		
		f1Label.setPreferredSize(new Dimension(180, 70));
		scrollPane.setPreferredSize(new Dimension(180, 240));
		
		// side panel
		sidePanel.setPreferredSize(new Dimension(200, 700));
		sidePanel.add(queryImagePanel);
		sidePanel.add(featuresOptionPanel);
		sidePanel.add(searchBtn);
		sidePanel.add(f1Label);
		sidePanel.add(scrollPane);
		
		// results panel
		resultsPanel.setPreferredSize(new Dimension(700, 700));
		resultsPanel.setLayout(new GridLayout(4, 4, 5, 5));
		for (int i = 0; i < 16; i++) {
			resultsLabelArray[i] = new JLabel("Image " + (i + 1), SwingConstants.CENTER);
			resultsLabelArray[i].setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
			resultsLabelArray[i].setForeground(Color.LIGHT_GRAY);
			resultsLabelArray[i].addMouseListener(imageClickedListener);
			resultsPanel.add(resultsLabelArray[i]);
		}
		
		BorderLayout borderLayout = new BorderLayout();
		contentPane.setLayout(borderLayout);
		contentPane.add(resultsPanel, BorderLayout.CENTER);
		contentPane.add(sidePanel, BorderLayout.LINE_START);
		
		setVisible(true);
		repaint();
	}

	public static void main(String[] args) throws IOException {
		ImageSearchUI ui = new ImageSearchUI();
	}

}