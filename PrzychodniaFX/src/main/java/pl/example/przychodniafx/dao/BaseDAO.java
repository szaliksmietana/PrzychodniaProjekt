package pl.example.przychodniafx.dao;

import pl.example.przychodniafx.DbConnection;
import pl.example.przychodniafx.model.User;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Abstrakcyjna klasa bazowa dla wszystkich klas DAO.
 * Zawiera wspólne metody używane przez różne DAO.
 */
public abstract class BaseDAO {
    
    /**
     * Pobiera połączenie z bazą danych.
     * 
     * @return Połączenie z bazą danych
     * @throws SQLException w przypadku błędu połączenia
     */
    protected Connection getConnection() throws SQLException {
        return DbConnection.getConnection();
    }
    
    /**
     * Ekstrahuje obiekt User z wyniku zapytania ResultSet.
     * 
     * @param rs ResultSet z danymi użytkownika
     * @return Obiekt User z danymi z ResultSet
     * @throws SQLException w przypadku błędu odczytu danych
     */
    protected User extractUserFromResultSet(ResultSet rs) throws SQLException {
        User user = new User(
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("pesel"),
                rs.getString("birth_date")
        );

        user.setUser_id(rs.getInt("user_id"));
        user.setLogin(rs.getString("login"));
        user.setPassword(rs.getString("password"));
        user.setAccess_level(rs.getInt("access_level"));
        
        // Bezpieczna obsługa wartości null
        Boolean isForgotten = rs.getObject("is_forgotten") != null ? 
                rs.getBoolean("is_forgotten") : false;
        user.setIs_forgotten(isForgotten);

        String genderStr = rs.getString("gender");
        if (genderStr != null && !genderStr.isEmpty()) {
            user.setGender(genderStr.charAt(0));
        }

        String roleName = rs.getString("role_name");
        if (roleName != null) {
            user.setRoleName(roleName);
        }
        
        return user;
    }
    
    /**
     * Sprawdza, czy wskazany identyfikator jest poprawny.
     * 
     * @param id identyfikator do sprawdzenia
     * @param name nazwa identyfikatora (używana w komunikacie błędu)
     * @throws IllegalArgumentException jeśli identyfikator jest niepoprawny
     */
    protected void validateId(int id, String name) {
        if (id <= 0) {
            throw new IllegalArgumentException(name + " ID must be positive");
        }
    }
} 