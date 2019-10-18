package com.webapp.timeline.sns.web;

public class BindingError {
    private String objectName = "";
    private String fieldName = "";
    private String fieldValue = "";
    private String message = "";
    private String code = "";

    public BindingError() {}

    public BindingError(String objectName, String fieldName, String fieldValue, String message, String code) {
        this.objectName = objectName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
        this.message = message;
        this.code = code;
    }

    static class BindingErrorBuilder {
        private String objectName = "";
        private String fieldName = "";
        private String fieldValue = "";
        private String message = "";
        private String code = "";

        public BindingErrorBuilder objectName(String objectName) {
            this.objectName = objectName;
            return this;
        }

        public BindingErrorBuilder fieldName(String fieldName) {
            this.fieldName = fieldName;
            return this;
        }

        public BindingErrorBuilder fieldValue(String fieldValue) {
            this.fieldValue = fieldValue;
            return this;
        }

        public BindingErrorBuilder message(String message) {
            this.message = message;
            return this;
        }

        public BindingErrorBuilder code(String code) {
            this.code = code;
            return this;
        }

        public BindingError build() {
            return new BindingError(objectName, fieldName, fieldValue, message, code);
        }
    }
}
