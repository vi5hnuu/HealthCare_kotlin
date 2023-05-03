package com.vi5hnu.healthcare

import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.CursorFactory
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

/*
*  SQLiteOpenHelper(@Nullable Context context, @Nullable String name,
            @Nullable CursorFactory factory, int version) {
        this(context, name, factory, version, null);
* */
class Database(context: Context, dbName: String, factory: CursorFactory?, version: Int) :
    SQLiteOpenHelper(context, dbName, factory, version) {
    companion object{
        var username:String="NULL"
    }
    override fun onCreate(db: SQLiteDatabase?) {
        val tableUserQuery="create table  IF NOT EXISTS users(username text primary key,email text not null,password text not null)";
        val tableDoctorsQuery="create table  IF NOT EXISTS doctors(" +
                "_id integer primary key, AUTO_INCREMENT," +
                "name text not null," +
                "hospital_address text," +
                "exp integer," +
                "mobile text not null," +
                "fee integer," +
                "description text," +
                "type text check(type in ('FD','DE','SU','CA','DI')) not null);"
        val statesTable="create table  IF NOT EXISTS states(" +
                "_id integer primary key," +
                "name text not null);"
        val citiesTable="create table  IF NOT EXISTS cities(" +
                "_id integer," +
                "name text not null," +
                "FOREIGN KEY (_id) REFERENCES states(_id));"
        val lab_testsTable="create table  IF NOT EXISTS lab_tests (" +
                "_id integer primary key ,AUTO_INCREMENT," +
                "name text not null," +
                "price double check(price >= 0));"
        val orderTable="create table  IF NOT EXISTS orders(" +
                "_id integer , AUTO_INCREMENT," +
                "name text,"+
                "amount number,"+
                "username text," +
                "order_id integer check(order_id>=0)," +
                "type text check(type in (\"T\",\"M\"))," +
                "FOREIGN KEY(username) REFERENCES users(username));"
        //in orderTable orderID is uid of labTest
        db?.execSQL(tableDoctorsQuery)
        db?.execSQL(tableUserQuery)
        db?.execSQL(statesTable)
        db?.execSQL(citiesTable)
        db?.execSQL(lab_testsTable)
        db?.execSQL(orderTable)
        this.initilizeDoctors(db)
        this.initilizeStates(db)
        this.initilizeCities(db)
        this.initilizeLabTests(db)
    }
    enum class DOCTORSCOLUMN(val identifier:String){
        NAME("name"),
        HOSPITAL_ADDRESS("hospital_address"),
        EXP("exp"),
        MOBILE("mobile"),
        FEE("fee"),
        DESCRIPTION("description"),
        TYPE("type"),
    }
    enum class DOCTOR_TYPE(val identifier:String){
        FAMILY_DOCTOR("FD"),
        DIETICIAN("DI"),
        SURGEON("SU"),
        CARDIOLOGIST("CA"),
        DENTIST("DE"),
    }
    enum class ORDER_TYPE(val identifier:String){
        LAB_TEST("T"),
        MEDICINE("M"),
    }
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }
    fun registerUser(username:String,email:String,password:String):Boolean{
        if(userExist(username)){
            return false
        }
        val cv=ContentValues()
        cv.put("username",username)
        cv.put("email",email)
        cv.put("password",password)
        val db:SQLiteDatabase=writableDatabase
        db.insert("users",null,cv)
        db.close()
        return true
    }

    fun login(usrnme: String,password: String):Boolean{
        if(!userExist(username, password)){
            return false
        }
        username=usrnme
        return true
    }
    fun getDoctorsCursor(type:DOCTOR_TYPE,vararg cols: DOCTORSCOLUMN):Cursor{
        val db:SQLiteDatabase=readableDatabase
        val columns = Array<String>(cols.size+1){"-"}
        columns[0]="_id"
        var i=0
        while(i<cols.size){
            columns[i+1]=cols[i].identifier
            i++
        }

        val cursor: Cursor = db.query("doctors",columns,"type=?", arrayOf(type.identifier),null,null,null)
        return cursor
    }
    fun getLabTestCursor():Cursor{
        val db:SQLiteDatabase=readableDatabase
        val columns= arrayOf("_id","name","price")
        val cursor=db.query("lab_tests",columns,null,null,null,null,null)
        return cursor
    }
    fun getDocDetails(id: Int): Cursor {
        val db: SQLiteDatabase = readableDatabase
        return db.rawQuery("select * from doctors where _id=?", arrayOf(id.toString()))
    }
    private fun userExist(username: String):Boolean{
        val db:SQLiteDatabase=readableDatabase
        val credentials = arrayOf(username)

        val cursor=db.rawQuery("select username from users where username=?",credentials)
        val result:Boolean=cursor.moveToFirst();
        cursor.close()
        return  result
    }
    private fun userExist(username: String,password: String):Boolean{
        val db:SQLiteDatabase=readableDatabase
        val credentials = arrayOf(username,password)
        val cursor=db.rawQuery("select username from users where username=? and password=?",credentials)
        val result:Boolean=cursor.moveToFirst();
        cursor.close()
        return  result
    }
    fun addToOrders(order_id:String,name:String,amount:String,type:ORDER_TYPE){//order_id is id of test/medicine
        if(orderAlreadyExist(username,order_id,type)){
            return
        }
        Log.d("ORDERS",order_id)
        val cv=ContentValues()
        cv.put("name",name)
        cv.put("amount",amount)
        cv.put("username",username)
        cv.put("order_id",order_id)
        cv.put("type",type.identifier)
        val db:SQLiteDatabase=writableDatabase
        db.insert("orders",null,cv)
        db.close()
    }
    fun getOrders():Cursor{
        val db:SQLiteDatabase=readableDatabase
        val cursor=db.query("orders", arrayOf("_id","name","amount","order_id","type"),null,null,null,null,null)
        return cursor
    }
    fun getOrderTotal():String{
        val db:SQLiteDatabase=readableDatabase
        val cursor=db.rawQuery("select sum(amount) from orders;", null)
        var total="0"
        if(cursor.moveToFirst()){
            total= cursor.getString(0)
        }
        cursor.close()
        return total;
    }
    fun removeFromOrders(order_id:String,type:ORDER_TYPE){
        if(!orderAlreadyExist(username,order_id,type)){
            return
        }
        val db:SQLiteDatabase=writableDatabase
        db.delete("orders","username=? and order_id=? and type=?", arrayOf(username,order_id,type.identifier))
        db.close()
    }
    private fun orderAlreadyExist(username: String,order_id: String,type:ORDER_TYPE):Boolean{
        val db:SQLiteDatabase=readableDatabase
        val cursor=db.query("orders",null,"username=? and order_id=? and type=?", arrayOf(username,order_id,type.identifier),null,null,null)
        if(cursor.moveToFirst()){
            cursor.close()
            return true;
        }
        cursor.close()
        return false;
    }
    private fun  initilizeDoctors(db:SQLiteDatabase?){
            val query="""
            insert into doctors (_id, name, hospital_address, exp, mobile, fee, description, type) values (1, 'Odetta', '6439 Independence Avenue', 5, '255 408 1204', 2705, 'Nondisplaced comminuted fracture of shaft of right fibula, subsequent encounter for open fracture type I or II with routine healing', 'DE')
            ,(2, 'Nolly', '783 Jenifer Junction', 9, '660 358 2995', 2399, 'Displaced fracture of navicular [scaphoid] of unspecified foot, initial encounter for closed fracture', 'FD')
            ,(3, 'Lisbeth', '533 Chinook Terrace', 2, '555 399 7301', 2600, 'Minor laceration of left vertebral artery, subsequent encounter', 'DI')
            ,(4, 'Kailey', '9839 Florence Center', 10, '290 764 7055', 3768, 'Other fracture of lower end of right femur, subsequent encounter for closed fracture with malunion', 'DE')
            ,(5, 'Drake', '80940 Lakewood Place', 7, '232 222 0450', 4334, 'Patellar tendinitis, unspecified knee', 'DI')
            ,(6, 'Brunhilde', '4 Canary Plaza', 10, '459 575 8201', 2762, 'Laceration without foreign body of abdominal wall, periumbilic region with penetration into peritoneal cavity, subsequent encounter', 'DE')
            ,(7, 'Jordon', '53 Mendota Way', 9, '104 268 8321', 4888, 'Other rupture of muscle (nontraumatic), left hand', 'DE')
            ,(8, 'Hodge', '6124 Banding Crossing', 4, '514 299 7423', 1227, 'Mucopolysaccharidosis, type II', 'DI')
            ,(9, 'Prentice', '0626 Lawn Center', 8, '436 714 4736', 1364, 'Contusion of prostate', 'DI')
            ,(10, 'Ella', '5958 West Trail', 3, '602 381 6136', 1737, 'Open bite of unspecified buttock, sequela', 'CA')
            ,(11, 'Demetra', '187 Basil Junction', 6, '838 211 8564', 3529, 'Poisoning by coronary vasodilators, accidental (unintentional), sequela', 'FD')
            ,(12, 'Westleigh', '01 Gina Street', 9, '272 612 8879', 1171, 'Corrosions of other specified parts of left eye and adnexa, initial encounter', 'SU')
            ,(13, 'Roxane', '018 Weeping Birch Junction', 7, '485 712 5963', 3659, 'Displaced spiral fracture of shaft of ulna, right arm, subsequent encounter for open fracture type IIIA, IIIB, or IIIC with routine healing', 'FD')
            ,(14, 'Amara', '7530 Hintze Pass', 7, '907 469 8346', 1873, 'Poisoning by therapeutic gases, intentional self-harm, subsequent encounter', 'DE')
            ,(15, 'Dian', '0910 Basil Park', 6, '127 957 0844', 1545, 'Hit or struck by falling object due to accident to canoe or kayak, initial encounter', 'SU')
            ,(16, 'Woodrow', '1070 Esch Terrace', 6, '289 597 9193', 4480, 'Displaced fracture of first metatarsal bone, unspecified foot, subsequent encounter for fracture with malunion', 'FD')
            ,(17, 'Skylar', '43 Texas Lane', 3, '736 677 0143', 2328, 'Military operations involving accidental detonation of onboard marine weapons, civilian, subsequent encounter', 'CA')
            ,(18, 'Sharon', '9 Westend Circle', 4, '907 137 8310', 4610, 'Poisoning by other hormone antagonists, accidental (unintentional), initial encounter', 'CA')
            ,(19, 'Carla', '7534 Kedzie Parkway', 5, '413 617 2438', 3803, 'Displaced spiral fracture of shaft of unspecified fibula, subsequent encounter for closed fracture with routine healing', 'CA')
            ,(20, 'Chiarra', '0166 Rusk Road', 7, '541 840 5759', 2271, 'Laceration of left quadriceps muscle, fascia and tendon', 'SU')
            ,(21, 'Tobit', '6559 Glacier Hill Hill', 2, '682 403 3133', 2802, 'Hyposplenism', 'SU')
            ,(22, 'Evie', '6 Acker Road', 6, '660 576 8447', 2094, 'Puncture wound without foreign body of left upper arm', 'DI')
            ,(23, 'Sigismond', '004 Miller Lane', 5, '244 480 8242', 2232, 'Human immunodeficiency virus [HIV] disease complicating the puerperium', 'DE')
            ,(24, 'Laurene', '62013 Bunting Trail', 7, '956 887 2515', 3569, 'Pruritus vulvae', 'SU')
            ,(25, 'Kaye', '1396 Drewry Place', 4, '702 204 9068', 1798, 'Poisoning by other agents primarily affecting the cardiovascular system, intentional self-harm', 'FD')
            ,(26, 'Nikoletta', '66355 Amoth Alley', 2, '632 351 9511', 4876, 'Sepsis of newborn due to Escherichia coli', 'CA')
            ,(27, 'Junette', '88309 Ohio Trail', 5, '537 212 1569', 4449, 'Ophthalmoplegic migraine, not intractable', 'SU')
            ,(28, 'Delia', '8 Linden Park', 7, '345 908 8168', 1432, 'Other mechanical complication of other bone devices, implants and grafts, initial encounter', 'DE')
            ,(29, 'Meg', '5889 Veith Pass', 7, '173 975 4672', 1514, 'Unspecified complication of internal prosthetic device, implant and graft', 'DI')
            ,(30, 'Bamby', '8356 West Trail', 5, '258 561 7542', 3460, 'Crushing injury of scrotum and testis, initial encounter', 'FD')
            ,(31, 'Sauncho', '278 Carey Lane', 2, '910 936 5017', 4155, 'Maternal care for known or suspected fetal abnormality and damage', 'DE')
            ,(32, 'Cullie', '360 Rusk Alley', 3, '461 495 7447', 4538, 'Displaced transverse fracture of shaft of left fibula, subsequent encounter for open fracture type I or II with malunion', 'SU')
            ,(33, 'Fons', '48318 Linden Trail', 8, '416 672 4831', 4880, 'Military operations involving unspecified fire, conflagration and hot substance, civilian', 'DE')
            ,(34, 'Freeland', '64 Thierer Plaza', 8, '759 883 5235', 3768, 'Ischemic cardiomyopathy', 'SU')
            ,(35, 'Korry', '2412 Springs Park', 10, '801 324 1859', 2704, 'Passenger of dune buggy injured in nontraffic accident, initial encounter', 'FD')
            ,(36, 'Sanson', '25962 Barby Parkway', 5, '494 558 2118', 1687, 'Maternal care for hydrops fetalis, second trimester, fetus 3', 'CA')
            ,(37, 'Harriot', '84 Pond Crossing', 7, '436 826 9044', 1188, 'Strain of intrinsic muscle, fascia and tendon of left ring finger at wrist and hand level, sequela', 'SU')
            ,(38, 'Annissa', '39065 Sheridan Trail', 7, '786 273 3051', 4015, 'Torus fracture of lower end of left ulna, initial encounter for closed fracture', 'DI')
            ,(39, 'Ansell', '83 Carey Pass', 9, '302 680 1427', 1197, 'Unspecified injury of popliteal vein, unspecified leg, initial encounter', 'DI')
            ,(40, 'Inglebert', '84946 Village Green Parkway', 2, '531 472 4323', 4720, 'Poisoning by dental drugs, topically applied, accidental (unintentional)', 'CA')
            ,(41, 'Kinnie', '32 Sommers Alley', 2, '477 880 8099', 4593, 'Toxic effect of contact with other venomous plant, intentional self-harm', 'CA')
            ,(42, 'Donetta', '1689 Cherokee Point', 5, '973 706 1722', 1987, 'Blood donor', 'FD')
            ,(43, 'Averyl', '93071 Waxwing Place', 4, '669 117 3341', 1836, 'Postprocedural hematoma of left eye and adnexa following an ophthalmic procedure', 'FD')
            ,(44, 'Stern', '1705 Hansons Point', 5, '689 873 8270', 2711, 'Drowning and submersion due to unspecified watercraft sinking, initial encounter', 'CA')
            ,(45, 'Kristo', '51 Chive Hill', 8, '974 354 4616', 1641, 'Hypertensive crisis', 'SU')
            ,(46, 'Sherill', '44356 Hayes Drive', 7, '127 351 2959', 4803, 'Displaced comminuted fracture of shaft of humerus, right arm, subsequent encounter for fracture with nonunion', 'CA')
            ,(47, 'Trula', '54710 Nancy Avenue', 5, '113 625 4425', 4990, 'Unspecified open wound of left back wall of thorax with penetration into thoracic cavity, sequela', 'DE')
            ,(48, 'Rickie', '0292 Stuart Court', 10, '491 933 5392', 1960, 'Whooping cough due to Bordetella pertussis with pneumonia', 'DE')
            ,(49, 'Mab', '785 Elka Crossing', 6, '734 916 9023', 1502, 'Other disorders of meninges, not elsewhere classified', 'SU')
            ,(50, 'Selig', '23068 Graceland Court', 2, '989 239 1779', 3074, 'Nondisplaced fracture of olecranon process without intraarticular extension of left ulna, initial encounter for open fracture type IIIA, IIIB, or IIIC', 'CA')
            ,(51, 'Gena', '9 Melrose Trail', 7, '896 874 0237', 3683, 'Family history of consanguinity', 'DE')
            ,(52, 'Donnajean', '524 Buhler Alley', 4, '329 803 5479', 4273, 'Acute pulmonary edema', 'DI')
            ,(53, 'Zondra', '72 Bartillon Alley', 9, '628 787 4526', 1059, 'Open bite of left lesser toe(s) with damage to nail, initial encounter', 'CA')
            ,(54, 'Cilka', '157 Knutson Parkway', 3, '709 669 8297', 1196, 'Chronic myelomonocytic leukemia, in remission', 'DE')
            ,(55, 'Bartlet', '39 Grasskamp Hill', 9, '312 602 9694', 3223, 'Fourth degree perineal laceration during delivery', 'CA')
            ,(56, 'Ketty', '13313 Brentwood Pass', 9, '413 752 0278', 3870, 'Nondisplaced spiral fracture of shaft of ulna, right arm, subsequent encounter for open fracture type I or II with malunion', 'SU')
            ,(57, 'Bennie', '47761 Graceland Drive', 9, '174 615 2657', 2327, 'Unspecified sprain of left wrist, sequela', 'DE')
            ,(58, 'Garner', '5525 Magdeline Terrace', 3, '253 185 9647', 2750, 'Other psychoactive substance abuse with intoxication, uncomplicated', 'DI')
            ,(59, 'Eadmund', '7510 Jackson Lane', 5, '582 897 0787', 3686, 'Injury of lesser saphenous vein at lower leg level', 'SU')
            ,(60, 'Kellyann', '07 Garrison Pass', 2, '987 486 0766', 1492, 'Superficial foreign body of unspecified front wall of thorax', 'CA')
            ,(61, 'Vilma', '3160 Claremont Hill', 9, '219 564 8187', 4917, 'Urethral disorder, unspecified', 'CA')
            ,(62, 'Malvin', '2554 Duke Circle', 8, '784 234 9412', 3464, 'Other specified complications of genitourinary prosthetic devices, implants and grafts', 'CA')
            ,(63, 'Delinda', '23128 Del Sol Place', 2, '424 889 6369', 3175, 'Pathological fracture, right femur, sequela', 'DE')
            ,(64, 'Lombard', '7061 Eastwood Pass', 4, '969 129 1608', 4036, 'Nondisplaced fracture of proximal phalanx of right middle finger, subsequent encounter for fracture with nonunion', 'DI')
            ,(65, 'Michell', '7279 East Pass', 10, '808 240 4163', 1038, 'Salter-Harris Type III physeal fracture of phalanx of left toe', 'DE')
            ,(66, 'Ree', '23 Kinsman Plaza', 2, '309 371 1206', 3277, 'Complex tear of lateral meniscus, current injury, left knee, subsequent encounter', 'CA')
            ,(67, 'Vladimir', '9 Lyons Drive', 5, '621 512 4173', 1812, 'Other physeal fracture of upper end of right fibula, initial encounter for closed fracture', 'CA')
            ,(68, 'Mae', '1899 Birchwood Drive', 3, '663 671 2676', 3373, 'Contact with workbench tool, sequela', 'CA')
            ,(69, 'Rosina', '384 Heath Plaza', 8, '241 913 9508', 2419, 'Contact with hot water in bath or tub', 'DI')
            ,(70, 'Ardenia', '70760 Garrison Avenue', 3, '531 437 5689', 4847, 'Crushing injury of left hip with thigh, initial encounter', 'CA')
            ,(71, 'Sumner', '8734 Portage Point', 4, '275 856 4722', 2114, 'Terrorism involving firearms, terrorist injured, initial encounter', 'DI')
            ,(72, 'Matthus', '4876 Green Ridge Crossing', 3, '243 965 4254', 4730, 'Unspecified atherosclerosis of unspecified type of bypass graft(s) of the extremities, unspecified extremity', 'CA')
            ,(73, 'Moss', '160 Magdeline Drive', 8, '793 363 2922', 1865, 'Toxic effect of other seafood, accidental (unintentional), sequela', 'CA')
            ,(74, 'Etti', '37944 Marcy Park', 10, '386 782 8277', 4773, 'Atrophoderma of Pasini and Pierini', 'DI')
            ,(75, 'Felicdad', '8 Barnett Park', 3, '836 791 1827', 4024, 'Other epidermolysis bullosa', 'CA')
            ,(76, 'Linet', '570 Mccormick Pass', 7, '398 447 1604', 3483, 'Displaced fracture (avulsion) of medial epicondyle of right humerus, initial encounter for closed fracture', 'FD')
            ,(77, 'Nananne', '1 Lukken Junction', 4, '597 529 7456', 2429, 'Car passenger injured in collision with pedestrian or animal in nontraffic accident, subsequent encounter', 'DI')
            ,(78, 'Hal', '618 Vernon Point', 8, '799 311 4521', 1007, 'Brown''s sheath syndrome, left eye', 'CA')
            ,(79, 'Garrett', '8545 Buena Vista Court', 3, '752 548 1371', 3772, 'Laceration of abdominal wall with foreign body, left lower quadrant without penetration into peritoneal cavity, subsequent encounter', 'SU')
            ,(80, 'Julianne', '3 Fremont Parkway', 5, '761 508 9930', 4630, 'Puncture wound with foreign body of right back wall of thorax without penetration into thoracic cavity, sequela', 'FD')
            ,(81, 'Cris', '781 Birchwood Place', 5, '503 309 3861', 3794, 'Subluxation of proximal interphalangeal joint of left thumb, subsequent encounter', 'SU')
            ,(82, 'Riane', '2 Rutledge Parkway', 10, '784 782 3426', 4806, 'Kidney donor', 'SU')
            ,(83, 'Shea', '7663 Dottie Pass', 10, '919 835 1963', 4136, 'Salter-Harris Type IV physeal fracture of lower end of humerus, unspecified arm, subsequent encounter for fracture with delayed healing', 'DE')
            ,(84, 'Lucho', '0 International Road', 10, '999 502 6965', 3599, 'Anterior scleritis', 'CA')
            ,(85, 'Harlin', '501 Randy Plaza', 10, '209 132 9757', 3892, 'Obstructed labor due to face presentation, fetus 1', 'CA')
            ,(86, 'Essy', '870 Esker Way', 10, '668 431 2573', 2753, 'Unspecified physeal fracture of lower end of unspecified fibula, subsequent encounter for fracture with routine healing', 'FD')
            ,(87, 'Emmi', '89 Bellgrove Parkway', 10, '549 786 1711', 4496, 'Transvestic fetishism', 'DI')
            ,(88, 'Wood', '94 Pankratz Point', 3, '216 529 4435', 4881, 'Displaced fracture of third metatarsal bone, left foot, subsequent encounter for fracture with routine healing', 'CA')
            ,(89, 'Nana', '48866 Burning Wood Center', 10, '196 465 5841', 3585, 'Oblique fracture of shaft of radius', 'DE')
            ,(90, 'Demeter', '83 Hanson Court', 9, '270 669 8804', 3900, 'Displaced comminuted fracture of shaft of right tibia, subsequent encounter for open fracture type I or II with delayed healing', 'FD')
            ,(91, 'Camille', '91995 Basil Parkway', 9, '473 272 6483', 1678, 'Unspecified fracture of upper end of unspecified tibia, subsequent encounter for open fracture type IIIA, IIIB, or IIIC with routine healing', 'CA')
            ,(92, 'Mozes', '3 Briar Crest Terrace', 7, '694 194 8669', 4345, 'Complete placenta previa NOS or without hemorrhage, third trimester', 'FD')
            ,(93, 'Adriena', '128 Esch Avenue', 6, '118 947 4447', 3517, 'Coma scale, eyes open, spontaneous, in the field [EMT or ambulance]', 'CA')
            ,(94, 'Ram', '18 Fuller Center', 7, '142 958 7568', 1652, 'Nondisplaced fracture of lesser tuberosity of right humerus, subsequent encounter for fracture with nonunion', 'DE')
            ,(95, 'Paxon', '40712 Killdeer Lane', 6, '602 382 1688', 3008, 'Maternal care for anti-D [Rh] antibodies, first trimester, not applicable or unspecified', 'DE')
            ,(96, 'Shaun', '94298 5th Lane', 4, '303 173 3244', 2195, 'Malocclusion, Angle''s class III', 'DI')
            ,(97, 'Donielle', '52 Thierer Terrace', 6, '917 555 4641', 2235, 'Contusion of eyeball and orbital tissues, left eye, sequela', 'CA')
            ,(98, 'Fey', '03 Scofield Place', 8, '883 443 0140', 1852, 'Nondisplaced fracture of lesser trochanter of left femur, subsequent encounter for open fracture type IIIA, IIIB, or IIIC with delayed healing', 'CA')
            ,(99, 'Adelbert', '5855 3rd Court', 5, '554 364 8291', 4127, 'Poisoning by ophthalmological drugs and preparations, intentional self-harm, sequela', 'CA')
            ,(100, 'Dede', '9877 Gulseth Junction', 2, '800 589 0317', 2086, 'Injury of unspecified iliac vein', 'SU');
        """.trimIndent()
        db?.execSQL(query)
    }
    private fun initilizeStates(db:SQLiteDatabase?){
        val query="""
            insert into states(_id, name) values
              (1, "Rajasthan"),
              (2, "Punjab"),
              (3, "Maharashtra");
        """.trimIndent()
        db?.execSQL(query)
    }
    private fun initilizeCities(db:SQLiteDatabase?){
        val query="""
            insert into cities(_id, name) values
              (1, "Abu"),
              (1, "Ajmer"),
              (1, "Alwar"),
              (1, "Amer"),
              (1, "Barmer"),
              (1, "Beawar"),
              (1, "Bharatpur"),
              (1, "Bhilwara"),
              (1, "Bikaner"),
              (1, "Bundi"),
              (1, "Chittaurgarh"),
              (1, "Churu"),
              (1, "Dhaulpur"),
              (1, "Dungarpur"),
              (1, "Ganganagar"),
              (1, "Hanumangarh"),
              (1, "Jaipur"),
              (1, "Jaisalmer"),
              (1, "Jalor"),
              (1, "Jhalawar"),
              (1, "Jhunjhunu"),
              (1, "Jodhpur"),
              (1, "Kishangarh"),
              (1, "Kota"),
              (1, "Merta"),
              (1, "Nagaur"),
              (1, "Nathdwara"),
              (1, "Pali"),
              (1, "Phalodi"),
              (1, "Pushkar"),
              (1, "Sawai Madhopur"),
              (1, "Shahpura"),
              (1, "Sikar"),
              (1, "Sirohi"),
              (1, "Tonk"),
              (1, "Udaipur"),
              (2, "Amritsar"),
              (2, "Batala"),
              (2, "Chandigarh"),
              (2, "Faridkot"),
              (2, "Firozpur"),
              (2, "Gurdaspur"),
              (2, "Hoshiarpur"),
              (2, "Jalandhar"),
              (2, "Kapurthala"),
              (2, "Ludhiana"),
              (2, "Nabha"),
              (2, "Patiala"),
              (2, "Rupnagar"),
              (2, "Sangrur"),
              (3, "Ahmadnagar"),
              (3, "Akola"),
              (3, "Amravati"),
              (3, "Aurangabad"),
              (3, "Bhandara"),
              (3, "Bhusawal"),
              (3, "Bid"),
              (3, "Buldhana"),
              (3, "Chandrapur"),
              (3, "Daulatabad"),
              (3, "Dhule"),
              (3, "Jalgaon"),
              (3, "Kalyan"),
              (3, "Karli"),
              (3, "Kolhapur"),
              (3, "Mahabaleshwar"),
              (3, "Malegaon"),
              (3, "Matheran"),
              (3, "Mumbai"),
              (3, "Nagpur"),
              (3, "Nanded"),
              (3, "Nashik"),
              (3, "Osmanabad"),
              (3, "Pandharpur"),
              (3, "Parbhani"),
              (3, "Pune"),
              (3, "Ratnagiri"),
              (3, "Sangli"),
              (3, "Satara"),
              (3, "Sevagram"),
              (3, "Solapur"),
              (3, "Thane"),
              (3, "Ulhasnagar"),
              (3, "Vasai - Virar"),
              (3, "Wardha"),
              (3, "Yavatmal");
        """.trimIndent()
        db?.execSQL(query)
    }
    private fun initilizeLabTests(db:SQLiteDatabase?){
        val query="""
            insert into lab_tests(name, price) values
              ("2D Echo", 112),
              ("4D Scan", 2),
              ("ACTH(Adreno Corticotropic Hormone) Test", 1025),
              ("Adenosine Deaminase Test", 27),
              ("AEC(Absolute Eosinophil Count) Test", 260),
              ("AFB(Acid Fast Bacilli) Culture Test", 87),
              ("AFP(Alpha Feto Protein) Test", 1996),
              ("Alberts Stain", 185),
              ("Albumin Test", 3386),
              ("Aldolase Test", 249),
              ("Alkaline Phosphatase(ALP) Test", 3298),
              ("Allergy Test", 292),
              ("Ammonia Test", 1246),
              ("Amylase Test", 326),
              ("ANA(Antinuclear Antibody) Test", 1770),
              ("ANC Profile", 1476),
              ("ANCA Profile", 466),
              ("Anti CCP(ACCP) Test", 843),
              ("Anti Phospholipid(APL) Test", 295),
              ("Anti TPO Test", 149),
              ("Anti - Mullerian Hormone(AMH) Test", 445),
              ("Antithyroglobulin Antibody Test", 1232),
              ("Antithyroid Microsomal Antibody(AMA) Test", 34),
              ("APTT(Activated Partial Thromboplastin Time) Test", 2766),
              ("Arterial Blood Gas(ABG)", 123),
              ("Ascitic Fluid Test", 126),
              ("ASO Test", 3174),
              ("Audiometry Test", 95),
              ("Beta HCG Test", 3461),
              ("Beta Thalassemia Test", 188),
              ("Bicarbonate Test", 1680),
              ("Bilirubin Test", 4607),
              ("Biopsy", 36),
              ("Bleeding / Clotting Time Test", 4715),
              ("Blood Culture Test", 3053),
              ("Blood Group Test", 5703),
              ("Blood Sugar Test", 6068),
              ("Blood Urea Nitrogen Test", 5258),
              ("Bone Density Test / Dexa Scan", 273),
              ("Bone Scan", 3),
              ("C - Peptide Test", 691),
              ("CA 15.3 Test", 1548),
              ("CA 19.9 Test", 1578),
              ("CA 27.29 Test", 1),
              ("CA - 125(Tumor Marker) Test", 2601),
              ("Calcium Test", 508),
              ("Carbamazepine(Tegretol) Test", 313),
              ("Cardiolipin Antibodies(ACL)", 50),
              ("CBC / Hemogram Test", 5492),
              ("CD4 Test", 99),
              ("CEA(Carcinoembryonic Antigen) Test", 1513),
              ("Cerebral Spinal Fluid(CSF) Test", 287);
            """.trimIndent()
        db?.execSQL(query)
    }
}