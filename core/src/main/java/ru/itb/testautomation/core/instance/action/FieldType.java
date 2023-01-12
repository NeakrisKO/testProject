package ru.itb.testautomation.core.instance.action;

public enum FieldType {
    ID {
        @Override
        public String toString() {
            return "ID";
        }
    }, NAME {
        @Override
        public String toString() {
            return "NAME";
        }
    }, CLASS_NAME {
        @Override
        public String toString() {
            return "CLASS_NAME";
        }
    }, TAG_NAME {
        @Override
        public String toString() {
            return "TAG_NAME";
        }
    },LINK_TEXT {
        @Override
        public String toString() {
            return "LINK_TEXT";
        }
    }, PARTIAL_LINK_TEXT {
        @Override
        public String toString() {
            return "PARTIAL_LINK_TEXT";
        }
    }, CSS_SELECTOR {
        @Override
        public String toString() { return "CSS_SELECTOR"; }
    }, XPATH {
        @Override
        public String toString() { return "XPATH"; }
    };

    @Override
    public abstract String toString();
}
