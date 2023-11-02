package application;
	
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.nio.file.Path;
import org.apache.lucene.document.Document;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
//import javafx.scene.shape.Path;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;

public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			
			AnchorPane root = new AnchorPane();
			TextField search_bar = new TextField();
			
			search_bar.setPrefWidth(540);
			search_bar.setPrefHeight(30);
			search_bar.setPromptText("Search");
			search_bar.setFocusTraversable(false);
	        root.getChildren().addAll(search_bar);
	        root.setTopAnchor(search_bar, 10.0);
	        root.setLeftAnchor(search_bar, 55.0);
	        
	        Button search_btn = new Button("Search");
	        search_btn.setPrefWidth(60);
	        search_btn.setPrefHeight(30);
	        root.getChildren().addAll(search_btn);
	        root.setTopAnchor(search_btn, 10.0);
	        root.setLeftAnchor(search_btn, 615.0);
	        
	        CheckBox  places_cb = new CheckBox("Places");
	        CheckBox people_cb = new CheckBox("People");
	        CheckBox title_cb = new CheckBox("Title");
	        CheckBox body_cb = new CheckBox("Body");
	        
	        HBox hb = new HBox(5);
	        hb.getChildren().addAll(places_cb,people_cb,title_cb,body_cb);
	        root.getChildren().addAll(hb);
    		root.setTopAnchor(hb, 50.0);
	        root.setLeftAnchor(hb, 55.0);
	        root.setRightAnchor(hb, 55.0);

	        search_btn.setOnAction(event -> {
	        	if(search_bar.getText().isEmpty()==false) {
	        		String checked_fields = "";
	        		
	        		if(places_cb.isSelected())
	        			checked_fields = checked_fields.concat("places;");
	        		if(people_cb.isSelected())
	        			checked_fields = checked_fields.concat("people;");
	        		if(title_cb.isSelected())
	        			checked_fields = checked_fields.concat("title;");
	        		if(body_cb.isSelected())
	        			checked_fields = checked_fields.concat("body;");
	        		
	        		Lucene srch = new Lucene(search_bar.getText() ,checked_fields);
	        		
	        		ObservableList<Document> items = FXCollections.observableArrayList(srch.docum);
	        		ListView<String> list = new ListView<>();
	        		Button open = new Button("Open Article");
	        		Button delete = new Button("Delete Article");
	        		
	        		list.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
	        		
	        		open.setPrefWidth(100);
	    	        open.setPrefHeight(30);
	    	        root.setBottomAnchor(open, 25.0);
	    	        root.setRightAnchor(open, 165.0);
                    root.getChildren().addAll(open);
                    
                    delete.setPrefWidth(100);
                    delete.setPrefHeight(30);
	    	        root.setBottomAnchor(delete, 25.0);
	    	        //root.setLeftAnchor(delete, 685.0);
	    	        root.setRightAnchor(delete, 55.0);
                    root.getChildren().addAll(delete);
                    	        		
	        		
	        		for(Document res : items) {
	        			String title = res.get(LuceneConstants.TITLE) + "\n";
	        			String places = res.get(LuceneConstants.PLACES);
	        			String people = res.get(LuceneConstants.PEOPLE) + "\n";
	        			String body  = res.get(LuceneConstants.BODY);
	        			title = title.replace("<TITLE>"," ");
	        			title = title.replace("</TITLE>"," ");
	        			places = places.replace("<PLACES>"," ");
	        			places = places.replace("</PLACES>"," ");
	        			people = people.replace("<PEOPLE>"," ");
	        			people = people.replace("</PEOPLE>"," ");
	        			body = body.replace("<BODY>"," ");
	        			body = body.replace("</BODY>"," ");
	        			if(body.length()>300) {
	        				char ch[] = new char[300];
	        				for (int i = 0; i < 297; i++) {
	        		            ch[i] = body.charAt(i);
	        		        }
	        				String str = "";
	        				for(char c : ch) {
	        					String s = Character.toString(c);
	        					str = str.concat(s);
	        				}
	        				str = str.concat("...");
		        			list.getItems().add(title + "\n" + str);
	        			}else {
	        				list.getItems().add(title + "\n" + body);
	        			}
	        			
	        			open.setOnAction(eventViewArticle -> {
	        				Stage article = new Stage();
	        				AnchorPane ap = new AnchorPane(); 
	        				
	        				Scene articleScene = new Scene(ap, 230, 100);
	        				if(list.getSelectionModel().getSelectedIndex()>=0) {
		        				Document d = items.get(list.getSelectionModel().getSelectedIndex());
		        				String t = d.get(LuceneConstants.TITLE), pl = d.get(LuceneConstants.PLACES), p = d.get(LuceneConstants.PEOPLE), b = d.get(LuceneConstants.BODY);
		        				t = t.replace("<TITLE>"," ");
		        				t = t.replace("</TITLE>"," ");
		        				pl = pl.replace("<PLACES>"," ");
		        				pl = pl.replace("</PLACES>"," ");
		        				p = p.replace("<PEOPLE>"," ");
		        				p = p.replace("</PEOPLE>"," ");
		        				b = b.replace("<BODY>"," ");
		        				b = b.replace("</BODY>"," ");
		        				Label title_label = new Label(t);
		        				Label people_label = new Label(p);
		        				Label places_label = new Label(pl);
		        				Label body_label = new Label(b);
		        				
		        				ap.setTopAnchor(title_label, 15.0);
		    	    	        ap.setLeftAnchor(title_label, 10.0);
		                        ap.getChildren().addAll(title_label);
		                        
		                        ap.setTopAnchor(people_label, 30.0);
		    	    	        ap.setLeftAnchor(people_label, 10.0);
		                        ap.getChildren().addAll(people_label);
		                        
		                        ap.setTopAnchor(places_label, 45.0);
		    	    	        ap.setLeftAnchor(places_label, 10.0);
		                        ap.getChildren().addAll(places_label);
		                        
		                        ap.setTopAnchor(body_label, 60.0);
		    	    	        ap.setLeftAnchor(body_label, 10.0);
		                        ap.getChildren().addAll(body_label);
		        				
		        				article.setTitle(d.get(LuceneConstants.FILE_NAME));
		        				article.setScene(articleScene);
		        				article.setHeight(450);
		        				article.setMinWidth(600);
		        				article.setMaxHeight(450);
		        				article.setMaxWidth(600);
		        				article.show();
	        				}
	        			});
	        			
	        			delete.setOnAction(eventDeleteArticle -> {
	        				if(list.getSelectionModel().getSelectedIndex()>=0) {
	        					Document d = items.get(list.getSelectionModel().getSelectedIndex());
	        					File file = new File("C:\\Users\\giorg\\eclipse-workspace\\TReSA\\Data\\" + d.get(LuceneConstants.FILE_NAME));
	        					String art_name = d.get(LuceneConstants.FILE_NAME);
	        					if(file.delete()) {
	        						System.out.println(art_name + " is deleted successfully!");
	        						items.remove(d);
			        				int selectedIdx = list.getSelectionModel().getSelectedIndex();
		        					list.getItems().remove(selectedIdx);
	        					}else {
	        						System.out.println("Failed to delete " + art_name);
	        					}
	        				}
	        			});
	        		}
	        		root.setTopAnchor(list, 80.0);
                    root.setLeftAnchor(list, 55.0);
                    root.setRightAnchor(list, 55.0);
                    root.setBottomAnchor(list, 80.0);
                    root.getChildren().addAll(list);
                    
	        	}
	        });
	        
	        Button edit = new Button("Edit");
	        edit.setPrefWidth(60);
	        edit.setPrefHeight(30);
	        root.getChildren().addAll(edit);
	        root.setTopAnchor(edit, 10.0);
	        root.setLeftAnchor(edit, 685.0);
	        
	        edit.setOnAction(edit_event -> {
	        	Stage currentStage = (Stage) edit.getScene().getWindow();
	        	
	            FileChooser fileChooser = new FileChooser();
	            fileChooser.setTitle("Choose files");
	            fileChooser.getExtensionFilters().addAll( new FileChooser.ExtensionFilter("TEXT files (*.txt)", "*.txt"));

	            File f = new File("C:\\Users\\giorg\\eclipse-workspace\\TReSA\\Reuters_Articles");
	            fileChooser.setInitialDirectory(f);
	            List<File> selectedFile = fileChooser.showOpenMultipleDialog(currentStage);
	            
	            if(selectedFile!=null) {
		            for(File file : selectedFile) {
		            	Path source = Paths.get(file.toString());
				        Path fileName = source.getFileName();
				        Path target = Paths.get("C:\\Users\\giorg\\eclipse-workspace\\TReSA\\Data\\" + fileName.toString());
		
				        System.out.println(fileName.toString() + " Added to Data");
				            
				        try {
							Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
						} catch (IOException e) {
							System.out.println("Exception while moving file: " + e.getMessage());
							e.printStackTrace();
						}
		            }
	            }

	            
	        	
	        });
	        
	        
	        Scene scene = new Scene(root,800,650);
	        primaryStage.setTitle("Search");
			primaryStage.setScene(scene);
			primaryStage.setHeight(650);
			primaryStage.setMinWidth(800);
			primaryStage.setMaxHeight(650);
			primaryStage.setMaxWidth(800);
			primaryStage.show();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
