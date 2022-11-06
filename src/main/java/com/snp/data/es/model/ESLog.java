package com.snp.data.es.model;

import static org.springframework.data.elasticsearch.annotations.FieldType.Text;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

@Document(indexName = "eslog")
public class ESLog {
	
	@Id
	private String id;
	
	@Field(type = Text)
    private String text;
	
	@Field(type = Text)
	private String type;
	
	@Field(type = Text)
	private String source;
	
	@Field(type = Text)
	private String user;

    public ESLog() {
    }

    public ESLog(String text) {
        this.text = text;
    }
    
    public ESLog(String text, String type, String source, String user) {
        this.text = text;
        this.type = type;
        this.source = source;
        this.user = user;
    }

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	@Override
	public String toString() {
		return "ESLog [id=" + id + ", text=" + text + ", type=" + type + ", source=" + source + ", user=" + user + "]";
	}
    
}
