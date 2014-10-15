package com.handybook.handybook;

abstract class DataManager {
    static enum Environment {P, S, Q1, Q2, Q3, Q4}
    private Environment env = Environment.S;

    Environment getEnvironment() {
        return env;
    }

    void setEnvironment(Environment env) {
        this.env = env;
    }

    abstract String[] getServices();

    abstract void authUser(String email, String password, Callback<User> cb);

    abstract void authFBUser(String fbid, String accessToken, String email, String firstName,
                             String lastName, Callback<User> cb);

    abstract void requestPasswordReset(String email, Callback<String> cb);

    static interface Callback<T> {
        void onSuccess(T response);
        void onError(DataManagerError error);
    }

    static enum Type {OTHER, SERVER, CLIENT, NETWORK}

    static final class DataManagerError {
        private final Type type;
        private final String message;
        private String[] invalidInputs;

        DataManagerError(final Type type) {
            this.type = type;
            this.message = null;
        }

        DataManagerError(final Type type, final String message) {
            this.type = type;
            this.message = message;
        }

        final String[] getInvalidInputs() {
            return invalidInputs;
        }

        final void setInvalidInputs(String[] inputs) {
            this.invalidInputs = inputs;
        }

        final String getMessage() {
            return message;
        }

        final Type getType() {
            return type;
        }
    }
}
