package eu.marcinszewczyk.db;

public class DbConfig {
    private String databaseUrl;
    private String username;
    private String password;
    private boolean shouldCreateSchema;

    public DbConfig(String databaseUrl, String username, String password, boolean shouldCreateSchema) {
        this.databaseUrl = databaseUrl;
        this.username = username;
        this.password = password;
        this.shouldCreateSchema = shouldCreateSchema;
    }

    public String getDatabaseUrl() {
        return databaseUrl;
    }

    public void setDatabaseUrl(String databaseUrl) {
        this.databaseUrl = databaseUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isShouldCreateSchema() {
        return shouldCreateSchema;
    }

    public void setShouldCreateSchema(boolean shouldCreateSchema) {
        this.shouldCreateSchema = shouldCreateSchema;
    }
}
