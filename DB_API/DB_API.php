<?php

class DB_API {

    
protected $dbConnection;

//************************//
//    Connection INFO     //
//************************//
function __construct($username){

    
    include 'config.php';
    

    
  
    $this->dbConnection = mysqli_connect($mysql_hostname, $mysql_user, $mysql_password, $mysql_database) or die("SQL ERROR: ". mysql_error());
    $this->dbConnection->set_charset("utf8");
}
    
    
    

    
    
//************************************************//
// Fetching PW,E-MAIL, fixing salt, change PW     //
//************************************************//    
    
    
    
function storeUser($email, $password, $student) {
    $uuid = uniqid('', true);
    $hash = $this->hashSSHA($password);
    
    $encrypted_password = $hash["encrypted"]; // encrypted password
    $salt = $hash["salt"]; // salt
    
    
    
    $sql = "INSERT INTO users(unique_id, email, encrypted_password, salt, student, coffee) VALUES(?, ?, ?, ?, ?, 0)";
    
    if ($stmt = mysqli_prepare($this->dbConnection, $sql)) {
        //bind parameters for markers
        mysqli_stmt_bind_param($stmt, "sssss", $uuid, $email, $encrypted_password, $salt, $student);

        //execute query
        $result = mysqli_stmt_execute($stmt);
        
        
        
        // check for successful store
        if ($result) {
            $stmt = $this->dbConnection->prepare("SELECT * FROM users WHERE email = ?");
            $stmt->bind_param("s", $email);
            $stmt->execute();
            $user = $stmt->get_result()->fetch_assoc();
            $stmt->close();
            return $user;
        } else {
            return false;
        }
    }
}
    

function getUserByEmailAndPassword($email, $password) {
    $sql = "SELECT * FROM users WHERE email = '" .$email ."'";
    $result = mysqli_query($this->dbConnection, $sql);
    
    //$user;
    while ($row = $result->fetch_array(MYSQLI_ASSOC)){
        $user = $row;
    }
    
    $salt = $user["salt"];
    $encrypted_password = $user['encrypted_password'];
    $hash = $this->checkhashSSHA($salt, $password);
    
    if ($hash == $encrypted_password){
        return $user;
    }else{
        return null;
    }
}
     
    
function isUserExisted($email) {
    $sql = "SELECT email from users WHERE email = '" .$email ."'";
    $result = mysqli_query($this->dbConnection, $sql);
    

    $data = array();
    
    
    while ($row = $result->fetch_array(MYSQLI_ASSOC)){
        $data[] = $row;
    }
    if (count($data) > 0){
        return true;
    }else {
        return false;
    }
}

    
function hashSSHA($password) {

    $salt = sha1(rand());
    $salt = substr($salt, 0, 10);
    $encrypted = base64_encode(sha1($password . $salt, true) . $salt);
    $hash = array("salt" => $salt, "encrypted" => $encrypted);
    return $hash;
}
    
    
function checkhashSSHA($salt, $password) {

    $hash = base64_encode(sha1($password . $salt, true) . $salt);

    return $hash;
}
    
    
function getUserByUUIDAndPassword($uuid, $password) {
    $sql = "SELECT * FROM users WHERE unique_id = '" .$uuid ."'";
    $result = mysqli_query($this->dbConnection, $sql);
    
    
    //$user;
    while ($row = $result->fetch_array(MYSQLI_ASSOC)){
        $user = $row;
    }
    
    $salt = $user["salt"];
    $encrypted_password = $user['encrypted_password'];
    $hash = $this->checkhashSSHA($salt, $password);
    
    if ($hash == $encrypted_password){
        return $user;
    }else{
        return null;
    }
}
    
    
    
    
    
    
    
    
    
    
    
function changePassword($uuid, $password) {
    
    $hash = $this->hashSSHA($password);
    
    $encrypted_password = $hash["encrypted"]; // encrypted password
    $salt = $hash["salt"]; // salt
    
    
    
    //$sql = "INSERT INTO users(unique_id, email, encrypted_password, salt, student, coffee) VALUES(?, ?, ?, ?, ?, 0)";
    
    $sql= 
        "UPDATE users 
         SET encrypted_password = ?, salt = ?
         WHERE unique_id = ?";
    
    
    
    if ($stmt = mysqli_prepare($this->dbConnection, $sql)) {
        //bind parameters for markers
        mysqli_stmt_bind_param($stmt, "sss", $encrypted_password, $salt, $uuid);

        //execute query
        $result = mysqli_stmt_execute($stmt);
        
        
        
        // check for successful store
        if ($result) {
            $stmt = $this->dbConnection->prepare("SELECT * FROM users WHERE unique_id = ?");
            $stmt->bind_param("s", $uuid);
            $stmt->execute();
            $user = $stmt->get_result()->fetch_assoc();
            $stmt->close();
            return $user;
        } else {
            return false;
        }
    }
}
    
    
    
    
    
    
    
    
    
    
    
    
    
    
//************************************************//
// Fetching menu for both students and employees  //
//************************************************//
    
    
    
 //======Fetching Menu students======
function getMenu() {
    $sql = "SELECT idMenu, merke, type, studentPris, kategori FROM Menu order by kategori desc";
    $result = mysqli_query($this->dbConnection, $sql);
    $data = array();
    while ($row = $result->fetch_array(MYSQLI_ASSOC)){
        $data[] = $row;
    }
    //Returnerer en array med spørring.
    return $data;
}
    
    
    
//======Fetching DrMeny students======
function getDrMenu() {
    //$sql = "SELECT idDRmeny, navn, serveringstid, studentPris, dag FROM DrMeny WHERE dag = DAYNAME(NOW());";
    $sql = "SELECT idDRmeny, navn, serveringstid, studentPris, dag, picture FROM DrMeny ORDER BY FIELD(dag,'MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY');";
    
    $result = mysqli_query($this->dbConnection, $sql);

    $data = array();
    while ($row = $result->fetch_array(MYSQLI_ASSOC)){
        $data[] = $row;
    }

    //Returnerer en array med spørring.
    return $data;
}
    
    
 //======Fetching Menu employees======
function getMenuEmployee() {
    //$sql = "SELECT idDRmeny, navn, serveringstid, ansattPris, dag FROM DrMeny WHERE dag = DAYNAME(NOW());";
    $sql = "SELECT idMenu, merke, type, ansattPris, kategori FROM Menu order by kategori desc;";
    $result = mysqli_query($this->dbConnection, $sql);
    $data = array();
    while ($row = $result->fetch_array(MYSQLI_ASSOC)){
        $data[] = $row;
    }
    //Returnerer en array med spørring.
    return $data;
}
    
    
    
//======Fetching DrMeny employees======
function getDrMenyEmployee() {
    //$sql = "SELECT idMenu, merke, type, ansattPris, kategori FROM Menu";
    $sql = "SELECT idDRmeny, navn, serveringstid, ansattPris, dag, picture FROM DrMeny ORDER BY FIELD(dag,'MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY');";

    $result = mysqli_query($this->dbConnection, $sql);

    $data = array();
    while ($row = $result->fetch_array(MYSQLI_ASSOC)){
        $data[] = $row;
    }

    //Returnerer en array med spørring.
    return $data;
}    
    


    


    
    
    
    
//*****************************************************//
// Fetching allergies for both menu and daily menu     //
//*****************************************************//
    
    
    
//get allergies on menu
function getMenuAllergier($id) {
    
    $sql = "SELECT Allergier.allergi FROM Allergier, Allergier_has_Menu where allergier_idAlergier = idAlergier AND menu_iDMenu = ?";
    
    if ($stmt = mysqli_prepare ($this->dbConnection, $sql)){    
        mysqli_stmt_bind_param($stmt, "s" ,$id);
        mysqli_stmt_execute($stmt);
        $result = $stmt->get_result();
        $data = array();
        
        while ($row = $result->fetch_array(MYSQLI_ASSOC)){
            $data[] = $row;   
        }
        //Returning array.
        return $data;
    }
    
}
    
    
    
//get allergies for dailymenu
function getDrMenyAllergier() {
    
    //$sql = "SELECT Allergier.allergi FROM Allergier, Allergier_has_DrMeny where allergier_idAlergier = idAlergier AND drmeny_idDRmeny = ?";
    $sql = "SELECT Allergier.allergi, allergier_idAlergier as AllergyID, drmeny_idDRmeny as menuID FROM Allergier, Allergier_has_DrMeny where allergier_idAlergier = idAlergier order by menuid";
    
    if ($stmt = mysqli_prepare ($this->dbConnection, $sql)){    
    mysqli_stmt_bind_param($stmt, "s" ,$id);
    mysqli_stmt_execute($stmt);
    $result = $stmt->get_result();
    $data = array();
        
        while ($row = $result->fetch_array(MYSQLI_ASSOC)){
            $data[] = $row;   
        }
        //Returning array.
        return $data;
    } 

}
    


    
    
    
    
//*****************************************************//
//          Queries for QR functionality               //
//*****************************************************//  
    
function checkRefillQR($qrString, $pin){
    
    $sql = "SELECT *
            FROM Coffee
            WHERE qrString = ? AND pin = ? AND description = 'refill'";
    
    if ($stmt = mysqli_prepare ($this->dbConnection, $sql)){    
        mysqli_stmt_bind_param($stmt, "ss" ,$qrString, $pin);
        mysqli_stmt_execute($stmt);
        $result = $stmt->get_result();
        $data = array();
        
        while ($row = $result->fetch_array(MYSQLI_ASSOC)){
            return $row;   
        }
    }
}
    
    
function checkBuyQR($qrString){
    
    $sql = "SELECT * 
            FROM Coffee 
            WHERE description = 'buy' AND qrString= ?";
    
    if ($stmt = mysqli_prepare ($this->dbConnection, $sql)){    
        mysqli_stmt_bind_param($stmt, "s" ,$qrString);
        mysqli_stmt_execute($stmt);
        $result = $stmt->get_result();
        $data = array();
        
        while ($row = $result->fetch_array(MYSQLI_ASSOC)){
            return $row;   
        }
    }
}
    
    
function checkCoffee($uuid){
    
    $sql = "SELECT coffee 
            FROM users
            WHERE unique_id = ?";
    
    if ($stmt = mysqli_prepare ($this->dbConnection, $sql)){    
        mysqli_stmt_bind_param($stmt, "s" ,$uuid);
        mysqli_stmt_execute($stmt);
        $result = $stmt->get_result();
        $data = array();
        
        while ($row = $result->fetch_array(MYSQLI_ASSOC)){
            return $row;   
        }
    }
}
    
    
function setCoffee($coffee, $uuid){
    
    $sql = "UPDATE users
            SET coffee = ?
            WHERE unique_id = ?";
    
    if ($stmt = mysqli_prepare ($this->dbConnection, $sql)){    
        mysqli_stmt_bind_param($stmt, "ss" ,$coffee, $uuid);
        
        //execute query
        $result = mysqli_stmt_execute($stmt);
        
        
        // check for successful store
        $sql = "SELECT coffee 
                FROM users 
                WHERE unique_id = ?";
        
        $stmt = $this->dbConnection->prepare($sql);
        $stmt->bind_param("s", $uuid);
        $stmt->execute();
        $user = $stmt->get_result()->fetch_assoc();
        $stmt->close();

        return $user["coffee"];
    }
}
     


    
  //DIV  
    
function setStudentStatus($isStudent, $uuid){
    
    $sql = "UPDATE users
            SET student = ?
            WHERE unique_id = ?";
    
    if ($stmt = mysqli_prepare ($this->dbConnection, $sql)){    
        mysqli_stmt_bind_param($stmt, "ss" ,$isStudent, $uuid);
        
        //execute query
        $result = mysqli_stmt_execute($stmt);
        
        
        // check for successful store
        $sql = "SELECT student 
                FROM users 
                WHERE unique_id = ?";
        
        $stmt = $this->dbConnection->prepare($sql);
        $stmt->bind_param("s", $uuid);
        $stmt->execute();
        $user = $stmt->get_result()->fetch_assoc();
        $stmt->close();

        return $user["student"];
    }
}
    
    
function getOpeningHours() {
    $sql = "SELECT openingHours, day, announcement FROM OpeningHours;";
    $result = mysqli_query($this->dbConnection, $sql);
    $data = array();
    while ($row = $result->fetch_array(MYSQLI_ASSOC)){
        $data[] = $row;
    }
    //Returnerer en array med spørring.
    return $data;
}
    
    
    
}
    
?>