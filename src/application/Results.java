package application;

import javafx.scene.control.Label;

public class Results {
	
	Label title = new Label();
	Label article = new Label();
	Label score = new Label();
	
	public Results() {
		
	}
	
	public Results(Label title,Label article, Float score) {
		this.title = title;
		this.article = article;
		this.score = new Label(score.toString());
	}
	
	public Label getTitle() {
		return title;
	}
	
	public Label getArticle() {
		return article;
	}
	
	public Label getScore() {
		return score;
	}


}
