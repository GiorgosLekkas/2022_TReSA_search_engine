package application;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RemoveTags {
	
	Pattern p_places = Pattern.compile("<PLACES>(.*)</PLACES>");
	Pattern p_people = Pattern.compile("<PEOPLE>(.*)</PEOPLE>");
	Pattern p_title = Pattern.compile("<TITLE>(.*)</TITLE>");
	Pattern p_body = Pattern.compile("<BODY>(.*)</BODY>");
	
	public String removePlaces(String places) {
		Matcher m_places = p_places.matcher(places);
		if( m_places.find() )
	        places = m_places.group(1).toString();
		return places;
	}
	
	public String removePeople(String people) {
		Matcher m_people = p_people.matcher(people);
		if( m_people.find() )
			people = m_people.group(1).toString();
		return people;
	}
	
	public String removeTitle(String title) {
		Matcher m_title = p_title.matcher(title);
		if( m_title.find() )
			title = m_title.group(1).toString();
		return title;
	}

	public String removeBody(String body) {
		Matcher m_body = p_body.matcher(body);
		if( m_body.find() )
			body = m_body.group(1).toString();
		return body;
	}
	
}
