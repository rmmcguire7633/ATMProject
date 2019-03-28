/**
 * The menu option for the implementing the add new atm, add new member and member actions.
 * */

package com.company;

import java.util.Scanner;

public class Menu {

  private Scanner scan = new Scanner(System.in, "UTF-8");
  private int userMenuOptionChoice;

  public void menuOption() {

    boolean whileRunning = true;

    while (whileRunning) {

      System.out.println("ATM Menu");
      System.out.println("Chose One Of The Options");
      System.out.println("1. Add new ATM");
      System.out.println("2. Add new member");
      System.out.println("3. Member Actions (Withdraw, check balance)");
      System.out.println("4. Exit");

      try {

        userMenuOptionChoice = scan.nextInt();

        if (userMenuOptionChoice == 1) {

          System.out.println("You chose 1");
          DataBaseProp dp = new DataBaseProp();

          dp.atmTtransaction();
        } else if (userMenuOptionChoice == 2) {

          System.out.println("You chose 2");

          DataBaseProp dp= new DataBaseProp();

          dp.insertNewMember();
        } else if (userMenuOptionChoice == 3 ) {

          System.out.println("Please select from the following");
          System.out.println("1. Check Balance in member account");
          System.out.println("2. Widthdraw from account");

          while(true) {

            try {

              userMenuOptionChoice = scan.nextInt();

              if (userMenuOptionChoice == 1) {

                System.out.println("You chose 1");
                DataBaseProp dp = new DataBaseProp();
                dp.getBalanceTotal();
              } else if (userMenuOptionChoice == 2) {

                System.out.println("you chose 2");
                DataBaseProp dp = new DataBaseProp();
                dp.withdraw();
              }
              break;
            } catch (Exception e) {

              scan.next();
              System.out.println("Please enter a valid number for the choice");
            }
          }
        } else if (userMenuOptionChoice == 4) {

          whileRunning = false;
        }else {

          System.out.println("Please enter a valid choice.");
        }
      } catch (Exception e) {

        scan.next();
        System.out.print("Please enter a valid number for the choice.");
      }
    }
  }
}
