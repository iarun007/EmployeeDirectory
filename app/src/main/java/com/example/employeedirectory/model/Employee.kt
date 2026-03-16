package com.example.employeedirectory.model

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.net.URL

data class Employee(
    val id: String, // Kept for backwards compatibility if needed, but we will mostly use cpfNo
    val name: String,
    val designation: String,
    val department: String,
    val cpfNo: String,
    val section: String,
    val extNo: String,
    val phoneNumber: String, // Added phone number
    val imageUrl: String? = null,
    val isBookmarked: Boolean = false
)

object EmployeeRepository {
    private val initialEmployees = listOf(
        Employee("73569","Burla Ravishankar","GGM(P)-HOI-IPEOT","HOI","73569","HOI","6900","9969228751"),
Employee("133660","Md. Imtiaz","SE (P)","HOI Office","133660","HOI Office","6901","8259950323"),
Employee("83245","Sabita Panda","Sr. HRE-PS TO HOI","HOI Office","83245","HOI Office","6902","9969228669"),
Employee("70056","DP Mhaskar","Chief F(W)","HOI Office","70056","HOI Office","6904","9969228682"),
Employee("83732","Manzoor Alam","DE","HOI Office","83732","HOI Office","6906","9819618537"),
Employee("70040","P.M. Koli","Manager (M&D)","HSE","70040","HSE","6905","9969228793"),
Employee("83154","Usha Pakala","CGM (P)–HOD","PRODUCTION ENGINEERING","83154","PRODUCTION ENGINEERING","6910","9490168664"),
Employee("83739","Purva Kadam","DE","PRODUCTION ENGINEERING","83739","PRODUCTION ENGINEERING","6915","9324733330"),
Employee("96172","Mayank Chahal","CE(P) Incharge","Well Stimulation & Sand Control","96172","Well Stimulation & Sand Control","6978","9426612465"),
Employee("123473","Rajnish Kumar","SE (P)","Well Stimulation & Sand Control","123473","Well Stimulation & Sand Control","6918","9426614349"),
Employee("131638","M.Q. Zaman","EE(P)","Well Stimulation & Sand Control","131638","Well Stimulation & Sand Control","6976","7506723497"),
Employee("96031","S.P. Matey","DGM(P)-Incharge","Well Services & WSO","96031","Well Services & WSO","6979","9428008700"),
Employee("121740","Devesh Tembhurkar","CE(P)","Well Services & WSO","121740","Well Services & WSO","6917","9428332059"),
Employee("134255","Umesh Kumar","EE (P)","Well Services & WSO","134255","Well Services & WSO","6977","7085030111"),
Employee("132063","T. Haji Mastan","EE (P)","Well Services & WSO","132063","Well Services & WSO","6919","9442500366"),
Employee("137472","Manisha","Sr.Chemist","Well Services & WSO","137472","Well Services & WSO","6913","7304443147"),
Employee("125370","Indu Singh","Supdt. Chemist","Production Lab","125370","Production Lab","6975","9410390369"),
Employee("136083","Mandeep","Sr. Chemist Incharge","Production Lab","136083","Production Lab","6911","8828304792"),
Employee("137249","Pooja Nain","Sr.Chemist","Production Lab","137249","Production Lab","6912","7304554152"),
Employee("136675","Nitish N Unhale","JTA(Chem)","Production Lab","136675","Production Lab","6914","7304951577"),
Employee("83734","Yashpal Rawat","DE","Production Lab","83734","Production Lab","","9920409045"),
Employee("83815","B. Harish Krishna","CGM (P)-Incharge","SURFACE FACILITIES PROCESS ENGINEERING AND RISK & RELIABILITY ENGG.","83815","SURFACE FACILITIES PROCESS ENGINEERING AND RISK & RELIABILITY ENGG.","6920","9442500583"),
Employee("83731","Neelam Deshmukh","DE","SURFACE FACILITIES PROCESS ENGINEERING AND RISK & RELIABILITY ENGG.","83731","SURFACE FACILITIES PROCESS ENGINEERING AND RISK & RELIABILITY ENGG.","6928","9552989562"),
Employee("95649","Mahendra P.Yadav","GM (P)-Incharge","SF & VAP(Plants)","95649","SF & VAP(Plants)","6941","9435716539"),
Employee("105438","T. Balakrishna","CE (P)","SF & VAP(Plants)","105438","SF & VAP(Plants)","6983","9427504455"),
Employee("123916","Ranjana Khanna","SE (P)","SF & VAP(Plants)","123916","SF & VAP(Plants)","6197","9643301567"),
Employee("105924","Jayesh Vasava","EE(P)","SF & VAP(Plants)","105924","SF & VAP(Plants)","6987","9429898034"),
Employee("135850","Shishir Jain","EE(P)","SF & VAP(Plants)","135850","SF & VAP(Plants)","6988","9531107877"),
Employee("137223","Priyanka","EE (P)","SF & VAP(Plants)","137223","SF & VAP(Plants)","6989","7452960608"),
Employee("82105","PVR Kherodkar","GM(P) Incharge","Installations","82105","Installations","6981","9428333908"),
Employee("91988","Rani Bora Borthakur","DGM (P)","Installations","91988","Installations","6982","9968282959"),
Employee("78406","D.P. Singh","GM(Mech)-Incharge","Risk & Reliability Engineering","78406","Risk & Reliability Engineering","6822","9868282203"),
        Employee("C. H. Sardar_RRE","C. H. Sardar","GM(Elec)","Risk & Reliability Engineering","","Risk & Reliability Engineering","",""),
Employee("106105","Arun Prakash Sahu","CE (P)","Risk & Reliability Engineering","106105","Risk & Reliability Engineering","6828","9969226885"),
Employee("106098","Shiv Ratan","CE ( E )","Risk & Reliability Engineering","106098","Risk & Reliability Engineering","6824","9428333920"),
Employee("92493","Gargee Bhattacharjee","CE (P)","Risk & Reliability Engineering","92493","Risk & Reliability Engineering","6893","9435718302"),
Employee("124973","R Praveen","SE(P)","Risk & Reliability Engineering","124973","Risk & Reliability Engineering","6892","9490168268"),
Employee("126531","Parmeshwar S Kannaujia","SE(P)","Risk & Reliability Engineering","126531","Risk & Reliability Engineering","6825","7574834158"),
Employee("105327","Sushil Daud","SE (Envt)","Risk & Reliability Engineering","105327","Risk & Reliability Engineering","6823","9428333927"),
Employee("134535","Deepalakshmi S","EE (Instt)","Risk & Reliability Engineering","134535","Risk & Reliability Engineering","6826","7710092094"),
Employee("137250","Manoj Yadav","EE (M)","Risk & Reliability Engineering","137250","Risk & Reliability Engineering","6827","8169934040"),
Employee("141341","G. Rahul Bhausaheb","JEA(Mech)","Risk & Reliability Engineering","141341","Risk & Reliability Engineering","6829","7738026152"),
Employee("125813","Manisha Meena","Suptdg.Chemist-I/c Process Lab","Process Labs","125813","Process Labs","6923","9428828037"),
Employee("135959","Dr. Utpal Kayal","Sr. Chemist","Process Labs","135959","Process Labs","6924","9930915013"),
Employee("137243","Ms. Arzoo Saini","Chemist","Process Labs","137243","Process Labs","6887","7304554151"),
Employee("136715","Rashmil Vernerkar","JTA(chem)","Process Labs","136715","Process Labs","6925","7304951575"),
Employee("139343","Yashasvi A. Kale","JTA(Chem)","Process Labs","139343","Process Labs","6927","7710090328"),
Employee("83737","Ramesh Chorghe","DE","Process Labs","83737","Process Labs","","9920492748"),
Employee("83845","Mangesh P. Kekre","CGM (P)-HOD","COMPOSITE MATERIAL CORROSION & SCALE MANAGEMENT","83845","COMPOSITE MATERIAL CORROSION & SCALE MANAGEMENT","6840","9428333200"),
Employee("83741","Madhumalini Mokal","DE","COMPOSITE MATERIAL CORROSION & SCALE MANAGEMENT","83741","COMPOSITE MATERIAL CORROSION & SCALE MANAGEMENT","6843","9820404906"),
Employee("96004","Manish Gupta","GM (P)-Incharge","Corrosion Management","96004","Corrosion Management","6845","9410391462"),
Employee("126950","Nikhil Khanduja","SE (P)","Corrosion  Management","126950","Corrosion  Management","6850","9428008674"),
Employee("134580","Suraj Makkar","Sr.Chemist","Corrosion  Management","134580","Corrosion  Management","6844","7710092096"),
Employee("134672","Simran Bareja","Sr. Chemist","Corrosion  Management","134672","Corrosion  Management","6846","7710092156"),
Employee("139593","Ravi","AEE ( M )","Corrosion  Management","139593","Corrosion  Management","6847","7710085297"),
Employee("140348","Rachana Tarte","JTA (Chemistry)","Corrosion  Management","140348","Corrosion  Management","6849","9527484551"),
Employee("71201","N. M. Bhoir","Head Worker (G)","Corrosion  Management","71201","Corrosion  Management","6848","9969228675"),
Employee("81656","Dipanka Baishya","GM (Elect)","Scale Management","81656","Scale Management","6841","7042191005"),
Employee("78248","Bharti Rawat","Gm (Chem) Incharge","Corrosion & Scale Lab","78248","Corrosion & Scale Lab","6921","9410390857"),
Employee("133637","Dibyajyoti Parida","Sr.Chemist","Corrosion & Scale Lab","133637","Corrosion & Scale Lab","6931","7710091790"),
        Employee("130688","Sushil Bhoye","AT (Chem)","Corrosion & Scale Lab","130688","Corrosion & Scale Lab","6932","9969228741"),
        Employee("Corrosion Labs_CSL","Corrosion Labs","6849, 6850, 6851","Corrosion & Scale Lab","","Corrosion & Scale Lab","",""),
Employee("70305","DKJ Narayana","CGM (P)-HOD","ARTIFICIAL LIFT & WELL ANALYSIS","70305","ARTIFICIAL LIFT & WELL ANALYSIS","6930","9428008656"),
Employee("83738","Manisha Bhoir","DE","ARTIFICIAL LIFT & WELL ANALYSIS","83738","ARTIFICIAL LIFT & WELL ANALYSIS","6933","9869186441"),
Employee("121740","Devesh Tembhurkar","CE(P) Incharge","ARTIFICIAL LIFT & WELL ANALYSIS","121740","ARTIFICIAL LIFT & WELL ANALYSIS","6917","9428332059"),
Employee("126881","KR Vijayvargia","SE(P)","Artificial Lift","126881","Artificial Lift","6945","9428332258"),
Employee("125302","Monika Meena","SE(P)","Artificial Lift","125302","Artificial Lift","6943","9428333314"),
Employee("135587","Aman Sharma","EE(P)","Artificial Lift","135587","Artificial Lift","6948","8828325738"),
Employee("137766","Pankaj Sharma","AEE (P)","Artificial Lift","137766","Artificial Lift","6946","7208934823"),
Employee("127077","Ankit Garg","SE (P)- Incharge","Artificial Lift Lab","127077","Artificial Lift Lab","6944","9435743994"),
Employee("136868","Vaibhav D. Pisal","Dy. Technician (P)","Artificial Lift Lab","136868","Artificial Lift Lab","6947","7066976260"),
Employee("82150","B. Prasad Rao","CGM (P)–HOD","Deep Water & Field Development","82150","Deep Water & Field Development","6940","9485136522"),
Employee("96022","Samit Pradhan","DGM (P)-Incharge","Deep Water & Field Development","96022","Deep Water & Field Development","6959","9428008330"),
Employee("123513","Vivek Prakash","SE (P)","Deep Water & Field Development","123513","Deep Water & Field Development","6953","9428332335"),
Employee("135489","Shivam Porwal","EE(P)","Deep Water & Field Development","135489","Deep Water & Field Development","6957","8828324172"),
Employee("136180","Shubham Gupta","AEE(P)","Deep Water & Field Development","136180","Deep Water & Field Development","6952","7042891381"),
Employee("139871","Asim Siddiqui ","AEE(P)","Deep Water & Field Development","139871","Deep Water & Field Development","6958","7710075421"),
Employee("104786","Anoop Yadhav","CE(P) Incharge","Flow Assurance","104786","Flow Assurance","6955","7042211515"),
Employee("125315","Vivek Singh","SE (P)","Flow Assurance","125315","Flow Assurance","6954","9428514723"),
Employee("81577","B.N.Baro","CGM(Civil) - HOD","STRUCTURES, ALTERNATE ENERGY & GEOTECHNICAL","81577","STRUCTURES, ALTERNATE ENERGY & GEOTECHNICAL","6810","8132978023"),
Employee("121924","Rajesh Kumar","CE (C)","Geotechnical","121924","Geotechnical","6832","9426612474"),
Employee("124995","Paras Vaid","SE (Civil)","Geotechnical","124995","Geotechnical","6835","9410391096"),
Employee("133068","Louis Doley","EE (Civil)","Geotechnical","133068","Geotechnical","6843","9402107750"),
        Employee("136183","C Sreenadh","EE (Civil)","Geotechnical","136183","Geotechnical","6833","8300010155"),
        Employee("Geotechnical Lab_GT","Geotechnical Lab","6837/6839","Geotechnical","","Geotechnical","",""),
Employee("122413","Manoj Kumar","SE(Civil) - Incharge","Structures","122413","Structures","6813","9428330534"),
Employee("125807","C. Sriram","SE(Civil)","Structures","125807","Structures","6815","9490168813"),
Employee("134538","Rachit Agarwal","EE (Civil)","Structures","134538","Structures","6816","8291281645"),
Employee("134235","Ankit Chauhan","EE (Civil)","Structures","134235","Structures","6814","8331998614"),
Employee("134744","Shakti Kumar","EE (Civil)","Structures","134744","Structures","6818","8291293624"),
Employee("140152","Shubham Wagh","AEE (Civil)","Structures","140152","Structures","6817","7710049260"),
Employee("123593","Ekta Jagir Prajapati","Head D/Man(Civil) ","Structures","123593","Structures","",""),
Employee("106020","Somali Pant Kund","CE (Civil) Incharge","Geotechnical  Lab","106020","Geotechnical  Lab","6838","9428828355"),
Employee("70038","A. H. Pawaskar","Cf. Suptd. D`man (C)","Geotechnical  Lab","70038","Geotechnical  Lab","6836","9969228686"),
Employee("96630","Vaibhav M Lavekar","Techn.(Production) ","Geotechnical  Lab","96630","Geotechnical  Lab","",""),
Employee("70307","Umesha Sodankoor","CGM(P)HOD-VII","NEW TECHNOLOGIES & TECHNICAL & SUPPORT","70307","NEW TECHNOLOGIES & TECHNICAL & SUPPORT","6800","9427504620"),
Employee("83745","Chandrakant N A","DE","NEW TECHNOLOGIES & TECHNICAL & SUPPORT","83745","NEW TECHNOLOGIES & TECHNICAL & SUPPORT","6805","9029534665"),
Employee("82361","SS Sodhi","GM (MM)","New Technology Trg.  BD MR","82361","New Technology Trg.  BD MR","6801","9868282220"),
Employee("125711","Kamal Singh","SE(P)","New Technology Trg.  BD MR","125711","New Technology Trg.  BD MR","6807","9428008369"),
Employee("133540","Mithlesh K Meena","Sr. MMO ","New Technology Trg.  BD MR","133540","New Technology Trg.  BD MR","6804","7042092226"),
Employee("83747","Sucheta Mhatre","DE","New Technology Trg.  BD MR","83747","New Technology Trg.  BD MR","6806","9969727695"),
Employee("64806","B.V.R.V. Prasad","CGM (P)","PCC","64806","PCC","6940","9485136522"),
Employee("106979","Sukumar Awasthi","CE(P) Incharge","PCC","106979","PCC","6961","9428008379"),
Employee("133406","Dhananjay Bhagat","SE (P)","PCC","133406","PCC","6956","7710091748"),
Employee("82602","S.P. Bhatt","GM (Security) Incharge","Administration","82602","Administration","6230","9969222006"),
Employee("92685","B.A Ghoderao","Asst HR Exe","Administration","92685","Administration","6882","9969225484"),
Employee("70893","S.P. Sodekar","Suptdg.(StenoEng)","Administration","70893","Administration","6234","9969228674"),
Employee("127832","Ms. Pranali Kharhade","Asst. P&A","Administration","127832","Administration","6232","9969228755"),
Employee("83742","JB Mahtre","DE","Administration","83742","Administration","6199","9769160121"),
Employee("83733","Kavita Chipade","DE","Administration","83733","Administration","6883","9920382079"),
Employee("94359","Sudha P Apte","GM(F&A) -Head","FINANCE & ACCOUNTS","94359","FINANCE & ACCOUNTS","6220/6970","9969220722"),
Employee("68860","Rashmi Behera","Manager(F&A)","FINANCE & ACCOUNTS","68860","FINANCE & ACCOUNTS","6228","9969224558"),
Employee("136229","Swati Singh","F&AO","FINANCE & ACCOUNTS","136229","FINANCE & ACCOUNTS","6227, 6971","7304449669"),
Employee("70158","R.W. Gaikwad","F&AO","FINANCE & ACCOUNTS","70158","FINANCE & ACCOUNTS","6229","9969225987"),
Employee("126615","Shreyas Jadhav","(F&A)","FINANCE & ACCOUNTS","126615","FINANCE & ACCOUNTS","6222","9969225319"),
Employee("70090","G.P. Gharat","Dy. Head Worker(field)","FINANCE & ACCOUNTS","70090","FINANCE & ACCOUNTS","6217","9969224978"),
Employee("83740","VH Thakur","DE","FINANCE & ACCOUNTS","83740","FINANCE & ACCOUNTS","6216","9869200111"),
Employee("92805","Yosada S Kadav","DE","FINANCE & ACCOUNTS","92805","FINANCE & ACCOUNTS","6225","9969397700"),
Employee("78553","Emerson Abraham","GM (E&T) Incharge","INFOCOM","78553","INFOCOM","6870","9428333231"),
Employee("82442","Mrs.Amrapali walade","GM (E&T)","INFOCOM","82442","INFOCOM","6871","9490168359"),
Employee("70483","Pradeep Bhosle","DGM (E&T)","INFOCOM","70483","INFOCOM","6872","9428333070"),
Employee("92737","Philomina Joseph Mrs.","SE(Elex)","INFOCOM","92737","INFOCOM","6873","9969228791"),
        Employee("83742_INFO","JB Mahtre","DE","INFOCOM","83742","INFOCOM","6199","9769160121"),
        Employee("Amruta Padnore_ES","Amruta Padnore","Sr.Project Fellow","ENERGY SECTION","","ENERGY SECTION","6861",""),
        Employee("76660","S. Rathinam","GM (Civil)","Civil Maintenance","76660","Civil Maintenance","6866","944300-9701"),
        Employee("Ganesh_CM","Ganesh","AEE (Civil)","Civil Maintenance","","Civil Maintenance","6858","7710046895"),
        Employee("94411","PS Shukla","","HR","94411","HR","7521","7042193930"),
        Employee("Ajay K. Parihar_HR","Ajay K. Parihar","DGM (HR)","HR","","HR","7564","9427504448"),
Employee("70037","Rohini Jadhav","Manager(HR)","Advance/Estt","70037","Advance/Estt","7534","9969228787"),
        Employee("139763","Ankita","Asst HRE","HR","139763","HR","7532","7710076103"),
        Employee("Jhunuka Das_HR","Jhunuka Das","","HR","","HR","7566","9969225976"),
        Employee("83629","Bindu P Nair","Manager(HR)","Contract","83629","Contract","7598","9969228673"),
        Employee("136713","Nawale Vikas Girish","","HR","136713","HR","2748/7594","7304986027"),
        Employee("81661","SS. Roy","GM (E )","Electrical maintenance","81661","Electrical maintenance","7584","942800-8118"),
        Employee("Kalpana Gohil_EM","Kalpana Gohil","DGM (E)","","","","6865",""),
        Employee("66311","Debjyoti Mahanta","DGM (E)","Electrical maintenance","66311","Electrical maintenance","6864","9969221309"),
        Employee("CM Khare_FS","CM Khare","","Fire Section","","Fire Section","6707, 6660, 6030","9969226775"),
        Employee("123860","Yogendra Singh Chauhan","ASO (Security)","Security Section","123860","Security Section","7772","9428519964"),
        Employee("136810","Arbind K. Singh","Security Supervisor","Security Section","136810","Security Section","","8657567098"),
        Employee("136910","Nilesh Shinde","Security Inspector","Security Section","136910","Security Section","7424","7304951579"),
        Employee("Richa Jain_HR","Richa Jain","Manager (HR)","HR","","HR","7524","9426614412"),
Employee("131979","TABASSUM","E2","PANVEL HR/ER","131979","PANVEL HR/ER","7564","9969228277"),
Employee("126695","Dr. Pankaj Bodade","ACMO","Medical","126695","Medical","7592","9485190543"),
Employee("92481","Dr Rabindra Das","ACMO","Medical","92481","Medical","7595","9435718592"),
Employee("131390","GOPALA KRISHNAN B. YADAV","Sr.Medical Officer","Medical","131390","Medical","","7574827006"),
Employee("83696","Jyoti Jangam","Sp Paramed (Optomet)","Medical","83696","Medical","7556","9969225209"),
Employee("70023","L V Mahajan","Sp Pharmacy (Allopathy)","Medical","70023","Medical","7429","9969224977"),
Employee("83677","Sushma Pawar","Sr Pharmacy Ex (Allopathy)","Medical","83677","Medical","7550","9969225993"),
Employee("83680","Jyoti Raut","Sr Pharmacy Ex (Allopathy)","Medical","83680","Medical","7550","9969225994"),
Employee("83691","S S Enugandul","Sr Pharmacy Ex (Allopathy)","Medical","83691","Medical","7579","9969225995"),
Employee("83678","P J Varghade","Sr Matron","Medical","83678","Medical","7548","9969224959"),
Employee("83675","S M Madhav","Sr Matron","Medical","83675","Medical","7548","9969224956"),
Employee("83676","N R Sawant","Sr Matron","Medical","83676","Medical","7592","9969224957"),
Employee("83688","J D Bagale","Sr Supdt (Radiology)","Medical","83688","Medical","7554","9969224994"),
Employee("83667","R R Karande","Sr Supdt (Nursing)","Medical","83667","Medical","7587","9969227587")
    )
    private val _employees = MutableStateFlow(initialEmployees)
    val employees: StateFlow<List<Employee>> = _employees.asStateFlow()
    
    suspend fun refreshEmployees() {
        withContext(Dispatchers.IO) {
            try {
                val url = "https://docs.google.com/spreadsheets/d/1C6YbIDQnwW-v1Ha1bcXzrWJCq8ZJxOGRA6N4GMo7bX4/export?format=csv"
                val csvContent = URL(url).readText()
                val lines = csvContent.lines()
                
                if (lines.size > 1) {
                    val newEmployees = lines.drop(1).filter { it.isNotBlank() }.mapNotNull { line ->
                        // Robust CSV splitting handling quotes
                        val parts = line.split(Regex(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"))
                            .map { it.trim().removeSurrounding("\"").replace("\"\"", "\"") }
                        
                        if (parts.size >= 5) { // Minimum requirement: Name, Dept, Section, Desig, CPF
                            val name = parts.getOrNull(0) ?: ""
                            val dept = parts.getOrNull(1) ?: ""
                            val section = parts.getOrNull(2) ?: ""
                            val desig = parts.getOrNull(3) ?: ""
                            val cpf = parts.getOrNull(4) ?: ""
                            val ext = parts.getOrNull(5) ?: ""
                            val mobile = parts.getOrNull(6) ?: ""
                            
                            Employee(
                                id = when {
                                    cpf.isNotBlank() -> cpf
                                    name.isNotBlank() -> "$name-$dept-$section".replace(" ", "_")
                                    else -> java.util.UUID.randomUUID().toString()
                                },
                                name = name,
                                designation = desig,
                                department = dept,
                                cpfNo = cpf,
                                section = section,
                                extNo = ext,
                                phoneNumber = mobile,
                                imageUrl = parts.getOrNull(8),
                                isBookmarked = false
                            )
                        } else null
                    }
                    if (newEmployees.isNotEmpty()) {
                        _employees.value = newEmployees
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    fun getEmployeeById(id: String): Employee? {
        return _employees.value.find { it.id == id }
    }

    fun toggleBookmark(employeeId: String) {
        _employees.value = _employees.value.map {
            if (it.id == employeeId) it.copy(isBookmarked = !it.isBookmarked) else it
        }
    }
}
