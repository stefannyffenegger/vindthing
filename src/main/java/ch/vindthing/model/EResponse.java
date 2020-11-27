package ch.vindthing.model;

public final class EResponse {

    public enum Store {
        STORE_ADD("Store Add: "),
        STORE_UPDATE("Store Update: "),
        ITEM_ADD("Item Add: "),
        ITEM_UPDATE("Item Update: "),
        ITEM_DELETE("Item Delete: ");

        private final String label;

        private Store(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }
}