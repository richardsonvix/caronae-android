package br.ufrj.caronae.models.modelsforjson;

public class FalaeMsgForJson {
    private String subject, message;

    public FalaeMsgForJson(String subject, String message) {
        this.subject = subject;
        this.message = message;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
