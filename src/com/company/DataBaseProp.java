/**
 * Allows the user to create a new ATM.
 * Allows the user to create a new member.
 * Allows the user to see their total balance.
 * Allows the user to withdraw from their balance and updates the ATM and Bank balance.
 * */

package com.company;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Scanner;

public class DataBaseProp {

  private final String DATABASEURL = "jdbc:sqlite:Database/ATM_Management.db";

  private final Connection connection = DriverManager.getConnection(DATABASEURL);

  private Scanner scan = new Scanner(System.in, "UTF-8");

  public DataBaseProp() throws SQLException {
  }

  /**
   * Creates a new field in the Bank table to hold its total balance.
   * */
  public void addBankTotalField(){

    final String inertTotalField = "ALTER Table BANK ADD BankTotal int";

    try {

      PreparedStatement add = connection.prepareStatement(inertTotalField);
      add.executeUpdate();
    } catch (Exception e) {

      System.out.print(e);
    }
  }

  /**
   * Updates the bank total field in the BANK table.
   * Gets a total balance based on account balances associated with that bank.
   * */
  public void addTotalsToBankField() throws SQLException {

    final String addToTotalField = "UPDATE Bank SET BankTotal = "
        + "(SELECT SUM(balance) FROM Account WHERE bank_id = Bank.bank_id);";

    try {

      PreparedStatement totals = connection.prepareStatement(addToTotalField);
      totals.executeUpdate();
    } catch (Exception e) {

      connection.close();
      System.out.print(e);
    }
  }

  /**
   * Starts the create new Atm transaction.
   * Creates a new ATM to place in the ATM table.
   * */
  public void atmTtransaction() throws SQLException {

    String insertATMSQL = "INSERT INTO ATM(atm_id, bank_id, atm_location," +
        " location_name, balance, num_of_tran)" +
        " VALUES (?, ?, ?, ?, ?, 0);";

    int counter = 0;
    connection.setAutoCommit(false);

    PreparedStatement insertATMtransaction = connection.prepareStatement(insertATMSQL);

    while (true) {

      try {

        insertATMtransaction.setInt(2, getBankID());
        insertATMtransaction.setInt(1, getATMid());
        insertATMtransaction.setInt(3, getATM_Location());
        insertATMtransaction.setString(4, getLocationName());
        insertATMtransaction.setInt(5, getBalancae());

        insertATMtransaction.addBatch();
        counter++;
      } catch (Exception e) {

        System.out.println("The batch could not be processed - rolled back to start");
        connection.rollback();
        connection.close();
        break;
      }

      System.out.println("Do you want to add any other ATMs? (Y/N)");
      if (scan.next().toLowerCase().startsWith("n")) {

        break;
      }
    }
    if (!connection.isClosed()) {

      System.out.println("There are " + counter + " rolles waiting to insert. Do you wish to"
          + " continue? (Y/N)\n");
      String crInput = scan.next().toLowerCase();

      if (crInput.startsWith("y")) {

        insertATMtransaction.executeBatch();
        connection.commit();
      } else {

        connection.rollback();
      }

      connection.close();
    }
  }

  /**
   * Gets the bank id for the create new ATM function.
   * @return the bank id entered by the user.
   * */
  public int getBankID() {

    final String getBankID = "SELECT bank_id FROM Bank;";
    int userInput = 0;

    boolean isTrue = true;

    while (isTrue) {

      try {

        PreparedStatement bankID = connection.prepareStatement(getBankID);

        System.out.println("Please Enter the number of the bank from the following:");
        ResultSet resultSet = bankID.executeQuery();

        while (resultSet.next()) {

          String bank_id = resultSet.getString("bank_id");
          System.out.println(bank_id);
        }

        userInput = scan.nextInt();
        isTrue = false;
      } catch (Exception e) {

        scan.next();
        System.out.println("Error: Please enter a correct bank id");
      }
    }

    return userInput;
  }

  /**
   * Gets the atm id for the create new atm function.
   * @return the atm id entered by the user.
   * */
  public int getATMid() {

    int atmID = 0;

    boolean isTrue = true;

    while (isTrue) {

      try {

        System.out.println("Please enter the id of the ATM");
        atmID = scan.nextInt();
        isTrue = false;
      } catch (Exception e) {

        scan.next();
        System.out.println("Please enter a correct number");
      }
    }
    return atmID;
  }

  /**
   * Gets the location id of the newly created ATM.
   * @return the atm location entered by the user.
   * */
  public int getATM_Location() {

    int ATM_location = 0;

    boolean isTrue = true;

    while (isTrue) {

      try {

        System.out.println("Please enter the ATM location ID");
        ATM_location = scan.nextInt();
        isTrue = false;
      } catch (Exception e) {

        scan.next();
        System.out.println("Please enter a valid location id");
      }
    }

    return ATM_location;
  }

  /**
   * Gets the location name where the new atm is located.
   * @return the location the user entered.
   * */
  public String getLocationName() {

    String location = null;

    boolean isTrue = true;

    while (isTrue) {

      try {

        System.out.println("Please enter the name of the location");

        scan.nextLine();

        location = scan.nextLine();
        isTrue = false;
      } catch (Exception e) {

        System.out.println("Please enter a valid location name");
        scan.next();
      }
    }

    return location;
  }

  /**
   * Gets the balance for the create ATM function.
   * @return the balance entered by the user.
   * */
  public int getBalancae() {

    int balance = 0;

    boolean isTrue = true;

    while (isTrue) {

      try {

        System.out.println("Please enter the balance in the ATM");

        balance = scan.nextInt();
        isTrue = false;
      } catch (Exception e) {

        System.out.println("Please enter a valid number for the balance");
        scan.next();
      }
    }

    return balance;
  }

  /**
   * Starts a transaction to add a new member to the member table.
   * Ask the user for input to fill in the fields of the Account and Member table.
   * */
  public void insertNewMember() throws SQLException {

    String insertNewMember = "INSERT INTO Member(mem_id, acct_id, mem_fname, mem_lname," +
        " ssn, phone, email, address, birthdate)" +
        " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";

    String insertIntoAccount = "INSERT INTO Account(acct_id, bank_id, acct_type, balance, is_active)" +
        " Values (?, ?, ?, ?, ?);";

    int counter = 0;
    connection.setAutoCommit(false);

    PreparedStatement insertMember = connection.prepareStatement(insertNewMember);
    PreparedStatement insertAccount = connection.prepareStatement(insertIntoAccount);

    while (true) {

      try {


        insertMember.setInt(1, getMemberID());
        int acctId = getAccct_id();
        insertMember.setInt(2, acctId);
        insertMember.setString(3, getFirstName());
        insertMember.setString(4, getLastName());
        insertMember.setInt(5, getSSN());
        insertMember.setInt(6, getPhoneNumber());
        insertMember.setString(7, getEmail());
        insertMember.setString(8, getAddress());
        insertMember.setDate(9, getBirthDate());

        insertMember.addBatch();

        insertAccount.setInt(1, acctId);
        insertAccount.setInt(2, getBankID());
        insertAccount.setString(3, getAccountType());
        insertAccount.setInt(4, getBalancae());
        insertAccount.setBoolean(5, true);

        insertAccount.addBatch();

        counter++;
      } catch (Exception e) {

        System.out.println("The batch could not be processed - rolled back to start");
        connection.rollback();
        connection.close();
        break;
      }

      System.out.println("Do you want to add any other members? (Y/N)");
      if (scan.next().toLowerCase().startsWith("n")) {

        break;
      }
    }

    if (!connection.isClosed()) {

      System.out.println("There are " + counter + " rolles waiting to insert. Do you wish to"
          + " continue? (Y/N)\n");
      String crInput = scan.next().toLowerCase();

      if (crInput.startsWith("y")) {

        insertMember.executeBatch();
        insertAccount.executeBatch();
        addTotalsToBankField();
        connection.commit();
      } else {

        connection.rollback();
      }

      connection.close();
    }
  }

  /**
   * Gets the member for the create new member function.
   * @return gets the memberid the user entered.
   * */
  public int getMemberID() {

    int memberid = 0;
    boolean isTrue = true;

    while (isTrue) {

      try {

        System.out.println("Please enter the member's id");

        memberid = scan.nextInt();
        isTrue = false;
      } catch (Exception e) {

        System.out.println("Please enter a valid member id");
        scan.next();
      }
    }

    return memberid;
  }

  /**
   * Gets the acct id for the create new member function.
   * @return the acct id entered by the user.
   * */
  public int getAccct_id() {

    int account_id = 0;
    boolean isTrue = true;

    while (isTrue) {

      try {

        System.out.println("Please enter the member's account id");

        account_id = scan.nextInt();
        isTrue = false;
      } catch (Exception e) {

        System.out.println("Please enter a valid account id number");
        scan.next();
      }
    }

    return account_id;
  }

  /**
   * Gets the first name for create new member function.
   * @return the firstname entered by the user.
   * */
  public String getFirstName() {

    String firstname = null;

    boolean isTrue = true;

    while (isTrue) {

      try {

        System.out.println("Please enter the members first name");

        scan.nextLine();

        firstname = scan.nextLine();
        isTrue = false;
      } catch (Exception e) {

        System.out.println("Please enter a valid first name");
        scan.next();
      }
    }

    return firstname;
  }

  /**
   * Gets the last name for the create new member function.
   * @return the last name entered by the user.
   * */
  public String getLastName() {

    String lastName = null;

    boolean isTrue = true;

    while (isTrue) {

      try {

        System.out.println("Please enter the members last name");

        lastName = scan.next();
        isTrue = false;
      } catch (Exception e) {

        System.out.println("Please enter a valid first name");
        scan.next();
      }
    }

    return lastName;
  }

  /**
   * Gets the ssn for the new member function.
   * @return the ssn the user entered.
   * */
  public int getSSN() {

    int ssn = 0;
    boolean isTrue = true;

    while (isTrue) {

      try {

        System.out.println("Please enter the member's ssn");

        ssn = scan.nextInt();
        isTrue = false;
      } catch (Exception e) {

        System.out.println("Please enter a valid ssn");
        scan.next();
      }
    }

    return ssn;
  }

  /**
   * Gets the phone number for the create new member function.
   * @return the phone number entered by the user.
   * */
  public int getPhoneNumber() {

    int phoneNumber = 0;
    boolean isTrue = true;

    while (isTrue) {

      try {

        System.out.println("Please enter the member's phone number");

        phoneNumber= scan.nextInt();
        isTrue = false;
      } catch (Exception e) {

        System.out.println("Please enter a valid phone number");
        scan.next();
      }
    }

    return phoneNumber;
  }

  /**
   * Gets the for the create new member function.
   * @return The email the user entered.
   * */
  public String getEmail() {

    String email = null;

    boolean isTrue = true;

    while (isTrue) {

      try {

        System.out.println("Please enter the member's email");

        scan.nextLine();

        email = scan.nextLine();
        isTrue = false;
      } catch (Exception e) {

        System.out.println("Please enter a valid email address");
        scan.next();
      }
    }

    return email;
  }

  /**
   * Gets the address entered by the user for creating a new member.
   * @return the address entered by the user.
   * */
  public String getAddress() {

    String address = null;

    boolean isTrue = true;

    while (isTrue) {

      try {

        System.out.println("Please enter the member's address in the format Street, city, State");


        address = scan.nextLine();
        isTrue = false;
      } catch (Exception e) {

        System.out.println("Please enter a valid address");
        scan.next();
      }
    }

    return address;
  }

  /**
   * Gets the birthdate for the new user creation entered by the user.
   * @return the birthdate entered by the user.
   * */
  public java.sql.Date getBirthDate() throws ParseException {

    String strTime = null;

    boolean isTrue = true;

    while (isTrue) {

      try {

        System.out.println("Please enter the member's birth date in the format yyyy-MM-dd");


        strTime = scan.next();
        isTrue = false;

      } catch (Exception e) {

        System.out.println("error");
        scan.next();
      }
    }

    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    java.util.Date d =  dateFormat.parse(strTime);
    java.sql.Date sqldate = new java.sql.Date(d.getTime());
    return sqldate;
  }

  /**
   * gets the account type information from the AcctType table.
   * Used to display options for the user to enter.
   * @return the account type the user entered.
   * */
  public String getAccountType() {

    final String getAccountType = "SELECT acct_type FROM AcctType;";
    String userInput = null;

    boolean isTrue = true;

    while (isTrue) {

      try {

        PreparedStatement accountType = connection.prepareStatement(getAccountType);

        System.out.println("Please Enter the account type from the following choices:");
        ResultSet resultSet = accountType.executeQuery();

        while (resultSet.next()) {

          String bank_id = resultSet.getString("acct_type");
          System.out.println(bank_id);
        }


        userInput = scan.next();
        isTrue = false;
      } catch (Exception e) {

        scan.next();
        System.out.println("Error: Please enter a correct bank id");
      }
    }

    return userInput;
  }

  /**
   * Gets the balance entered by the user.
   * @return the amount the user entered.
   * */
  public int getBalance() {

    int balance = 0;
    boolean isTrue = true;

    while (isTrue) {

      try {

        System.out.println("Please enter the member's balance");

        balance= scan.nextInt();
        isTrue = false;
      } catch (Exception e) {

        System.out.println("Please enter a valid balance number");
        scan.next();
      }
    }

    return balance;
  }

  /**
   * gets the balance of in the Account field.
   * Gets the user total balance from the database.
   * */
  public void getBalanceTotal() throws SQLException {

    int memberBalance = 0;
    int memberAcctId = 0;

    final String balanceSQL = "SELECT balance FROM Account WHERE ? = acct_id";
    PreparedStatement totalMemberBalance = connection.prepareStatement(balanceSQL);

    while (true) {

      System.out.println("Please enter the member's account id to view the balance");
      try {

        memberAcctId = scan.nextInt();
        totalMemberBalance.setInt(1, memberAcctId);

        ResultSet rs = totalMemberBalance.executeQuery();
        memberBalance = rs.getInt("balance");
        System.out.println("The balance is " + memberBalance);
        break;
      } catch (Exception e) {

        scan.next();
        System.out.println("The member account number entered does not exist.");
      }
    }
  }

  /**
   * This method ask the user for input and withdraws the amount they determined from the bank.
   * This method updates the bank BankTotal and Atm balance based on how much the user widthdrew.
   * */
  public void withdraw() throws SQLException {

    int userWithdraw = 0;
    int memberAcctID = 0;
    int atmID = 0;
    int totalMemberBalance = 0;
    int totalbankBalance = 0;

    connection.setAutoCommit(false);

    final String withdrawSQL = "UPDATE Account SET balance = balance - ? WHERE acct_id = ?;";
    final String withdrawATM = "UPDATE Atm SET balance = balance - ? WHERE atm_id = ?";


    final String withdrawBankSQL = "UPDATE Bank SET BankTotal = BankTotal - ? WHERE "
          + "(SELECT Account.bank_id FROM Account WHERE Account.acct_id = ?) = Bank.bank_id";

    final String balanceUser = "SELECT balance FROM Account WHERE ? = acct_id";

    final String bankBalanceSQL = "SELECT BankTotal FROM Bank WHERE ("
        + "SELECT Account.bank_id FROM Account WHERE Account.acct_id = ?) = Bank.bank_id;";

    PreparedStatement withdrawBank = connection.prepareStatement(withdrawBankSQL);
    PreparedStatement memberWithdraw = connection.prepareStatement(withdrawSQL);
    PreparedStatement atmWithdraw = connection.prepareStatement(withdrawATM);
    PreparedStatement balanceOfMember = connection.prepareStatement(balanceUser);

    PreparedStatement totalBankBalance = connection.prepareStatement(bankBalanceSQL);

    while (true) {

      System.out.println("please enter your account number to withdraw");

      try {
        memberAcctID = scan.nextInt();

        System.out.println("Please enter the amount you would like to withdraw");
        userWithdraw = scan.nextInt();

        System.out.println("Please enter the ATM's id that you are withdrawing from");
        atmID = scan.nextInt();

        memberWithdraw.setInt(1, userWithdraw);
        memberWithdraw.setInt(2, memberAcctID);

        atmWithdraw.setInt(1, userWithdraw);
        atmWithdraw.setInt(2, atmID);

        withdrawBank.setInt(1, userWithdraw);
        withdrawBank.setInt(2, memberAcctID);

        balanceOfMember.setInt(1, memberAcctID);

        totalBankBalance.setInt(1, memberAcctID);


        atmWithdraw.addBatch();
        memberWithdraw.addBatch();
        withdrawBank.addBatch();
        break;
      } catch (Exception e) {

        System.out.println("The batch could not be processed - rolled back to start");
        connection.rollback();
        connection.close();
      }
    }

    if (!connection.isClosed()) {

      System.out.println("Would you like to withdraw $" + userWithdraw + " from your account (Y/N)\n");
      String crInput = scan.next().toLowerCase();

      if (crInput.startsWith("y")) {

        memberWithdraw.executeBatch();
        atmWithdraw.executeBatch();
        withdrawBank.executeBatch();
        connection.commit();

        ResultSet rs = balanceOfMember.executeQuery();
        totalMemberBalance = rs.getInt("balance");

        ResultSet resultSet = totalBankBalance.executeQuery();
        totalbankBalance = resultSet.getInt("BankTotal");

        System.out.println("The total amount in the member account is " + totalMemberBalance);
        System.out.println("The total amount in the Bank is " + totalbankBalance);
      } else {

        connection.rollback();
      }

      connection.close();
    }
  }
}

