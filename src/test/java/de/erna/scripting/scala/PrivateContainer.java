package de.erna.scripting.scala;

public class PrivateContainer {
    public interface PublicInterface {
        String getName();
    }

    public class BaseClass implements PublicInterface {
        public String getName() {
            return "base";
        }
    }

    private class PrivateClass extends BaseClass implements PublicInterface {
        public PrivateClass() {}

        public String getName() {
            return "derived";
        }
    }
}
