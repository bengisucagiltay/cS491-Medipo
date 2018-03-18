package register;

import org.apache.commons.validator.routines.EmailValidator;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Scanner;

import utils.FileManager;
import static utils.FileManager.*;


/**
 * Servlet implementation class Register
 */
@WebServlet("/Register")
public class Register extends HttpServlet {
    private static final String USER_INFO = FileManager.getUsersFilePath();

    private static final String PASSWORDS = FileManager.getPasswordsFilePath();

    private File EMAILS = new File(FileManager.getEmailsFilePath());

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        String fname = request.getParameter("firstname");
        String lname = request.getParameter("lastname");
        String mail = request.getParameter("email");
        String pword = request.getParameter("password");

        PrintWriter out = response.getWriter();
        //TODO::
        //checkFileExists();

        //HttpSession session = request.getSession();
        //session.setAttribute("fname", fname);   //TODO: use this session attribute for WELCOME $fname message

        if (checkUserExists(mail) == true) {
            System.out.println("A user with this e-mail already exists!");
            alerts(out, "Oops", "A user with this e-mail already exists! Please try again..", "error");
            //response.sendRedirect("register.jsp");
            RequestDispatcher rd = request.getRequestDispatcher("register.jsp");
            rd.include(request, response);
        } else if (mail.equals("") || pword.equals("") || fname.equals("") || lname.equals("")) {
            System.out.println("Entry cannot be empty");
            alerts(out, "Oops", "Entry cannot be empty! Please try again..", "error");
            //response.sendRedirect("register.jsp");
            RequestDispatcher rd = request.getRequestDispatcher("register.jsp");
            rd.include(request, response);
        } else if (pword.length() < 1) {
            System.out.println("Password should be at least 8 characters");
            alerts(out, "Oops", "Password should have at least 8 characters! Please try again..", "error");
            RequestDispatcher rd = request.getRequestDispatcher("register.jsp");
            rd.include(request, response);
        }
        //TODO: CHECK IF EMAIL IS WRITTEN IN NAME@EMAIL.COM
        else if (validateMail(mail) == false) {
            System.out.println("Incorrect mail form (NAME@EMAIL.COM)");
            alerts(out, "Oops", "Incorrect mail form (NAME@EMAIL.COM)", "error");
            RequestDispatcher rd = request.getRequestDispatcher("register.jsp");
            rd.include(request, response);

        } else {
            writeUserInfo(fname, lname, mail, pword);
            getUserDirectoryPath(mail);
            alerts(out, "Success", "Register Complete! Please log in..", "success");
            RequestDispatcher rd = request.getRequestDispatcher("login.jsp");
            rd.include(request, response);
        }
    }

    private boolean validateMail(String mail) {
        EmailValidator emailValidator = EmailValidator.getInstance();

        if (emailValidator.isValid(mail))
            return true;
        else
            return false;
    }

    /*private void createUserHomeFile(String mail) {
        String filePath = FileManager.getResourcesDirectoryPath() + "/users/" + mail.replace('@', '-');
        File f = new File(filePath);
        f.mkdirs();
        return;
    }*/

    private void checkFileExists() {


        File users = new File(USER_INFO);
        File passwords = new File(PASSWORDS);
        File emails = new File(String.valueOf(EMAILS));

        try {
            if (!users.exists()) {
                users.getParentFile().mkdirs();
                users.createNewFile();
            }
            if (!passwords.exists()) {
                passwords.getParentFile().mkdirs();
                passwords.createNewFile();
            }
            if (!emails.exists()) {
                emails.getParentFile().mkdirs();
                emails.createNewFile();
            } else
                System.out.println("File Status unknown!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * private method checkIfExists
     * scans the email file and checks if an email already is registered
     * returns true if already registered
     * */
    private boolean checkUserExists(String mail) {
        try {
            Scanner scanner = new Scanner(EMAILS, "UTF-8");
            while (scanner.hasNextLine()) {
                if (mail.equals(scanner.nextLine()))
                    return true;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    /*
     * private method writeUserInfo
     * writes the user info to corresponded files
     * */
    private void writeUserInfo(String fname, String lname, String mail, String pword) {
        Writer wr1 = null;
        Writer wr2 = null;
        Writer wr3 = null;

        try {
            /*wr1 = new BufferedWriter(new FileWriter(USER_INFO,true));
            wr1.write(fname + "," + lname +"\n");
            wr2 = new BufferedWriter(new FileWriter(EMAILS,true));
            wr2.write(mail +"\n");
            wr3 = new BufferedWriter(new FileWriter(PASSWORDS,true));
            wr3.write(pword +"\n");*/
            wr1 = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(USER_INFO, true), "UTF8"));
            wr1.write(fname + "," + lname + "\n");
            wr2 = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(EMAILS, true), "UTF8"));
            wr2.write(mail + "\n");
            wr3 = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(PASSWORDS, true), "UTF8"));
            wr3.write(pword + "\n");

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (wr1 != null)
                    wr1.close();
                if (wr2 != null)
                    wr2.close();
                if (wr3 != null)
                    wr3.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Registration complete");
    }

    private void alerts(PrintWriter out, String alert, String message, String type) {

        out.println("<script src='https://cdnjs.cloudflare.com/ajax/libs/limonte-sweetalert2/6.11.4/sweetalert2.all.js'></script>");
        out.println("<script src='https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js'></script>");
        out.println("<script>");
        out.println("$(document).ready(function(){");
        out.println("swal ( '" + alert + "' ,  '" + message + "' ,  '" + type + "' )");
        out.println("});");
        out.println("</script>");
        //response.sendRedirect("register.jsp");

    }
}