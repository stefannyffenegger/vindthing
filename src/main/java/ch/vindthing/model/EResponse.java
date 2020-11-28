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

        @Override
        public String toString() {
            return label;
        }
    }

    public enum Item {
        STORE_ADD("Store Add: "),
        STORE_UPDATE("Store Update: "),
        ITEM_ADD("Item Add: "),
        ITEM_UPDATE("Item Update: "),
        ITEM_DELETE("Item Delete: ");

        private final String label;

        private Item(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }
}