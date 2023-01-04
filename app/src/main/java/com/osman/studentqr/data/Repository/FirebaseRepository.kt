package com.osman.studentqr.data.Repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.osman.studentqr.common.toUserName
import com.osman.studentqr.data.model.*
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FirebaseRepository {
    /*
    Login olma işlemi yapar login başarılı olma durumunda true, hatalı olma durumunda ise
    false değerini döner
     */
    suspend fun login(email: String, password: String, onComplete: (Boolean) -> Unit) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    onComplete(true)
                } else {
                    onComplete(false)
                }
            }
    }


    /*
        Kayıt olma işlemi yapar kayıt başarılı olma durumunda true, hatalı olma durumunda ise
        false değerini döner
         */
    suspend fun register(email: String, password: String, onComplete: (Boolean) -> Unit) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    onComplete(true)
                } else {
                    onComplete(false)
                }
            }
    }

    /*
    Kayıt olunan hesabın mail adresine doğrulama kodu gönderir, kod gönderme başarılı olma durumunda true, hatalı olma durumunda ise
    false değerini döner
     */
    suspend fun sendVerification(onComplete: (Boolean) -> Unit) {
        FirebaseAuth.getInstance().currentUser?.sendEmailVerification()?.addOnCompleteListener {
            if (it.isSuccessful) {
                onComplete(true)
            } else {
                onComplete(false)
            }
        }
    }

    /*
    kayıt olunan kullanıcıların mail adresini doğrulayıp doğrulamadıklarını kontrol eder,
    doğrulama başarılı olma durumunda true, hatalı olma durumunda ise false değerini döner
     */
    suspend fun checkIfVerified(onComplete: (Boolean) -> Unit) {
        FirebaseAuth.getInstance().currentUser?.isEmailVerified.let {
            if (it != null && it) {
                onComplete(true)
            } else {
                onComplete(false)
            }
        }
    }

    /*
    Kayıt olunan öğrenci ise kullanıcı bilgileri ile ekstra olarak veritabanındaki student tablosuna
    da kaydı yapılır bu sayede kullanıcı bilgilerini (eposta, ogrenci no vs) almak daha da kolaylaşır
     */
    suspend fun addStudentToDb(student: Student, onComplete: (Boolean) -> Unit) {
        student.studentMail?.toUserName()?.let {
            Firebase.database.reference.child("student").child(it).setValue(student)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        onComplete(true)
                    } else {
                        onComplete(false)
                    }
                }
        }
    }

    /*
    Kayıt olunan öğretmen ise kullanıcı bilgileri ile ekstra olarak veritabanındaki teacher tablosuna
    da kaydı yapılır bu sayede kullanıcı bilgilerini (eposta, öğretmen adı vs) almak daha da kolaylaşır
     */
    suspend fun addTeacherToDb(teacher: Teacher, onComplete: (Boolean) -> Unit) {
        teacher.teacherMail?.toUserName()?.let {
            Firebase.database.reference.child("teacher").child(it).setValue(teacher)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        onComplete(true)
                    } else {
                        onComplete(false)
                    }
                }
        }
    }

    /*
    Öğretmen yeni ders oluştura tıklayınca bu fonksiyon firebase'e ders oluşturma komutunu gönderir ve TeacherLesson data sınıfında
    bulunan özelliklerle birlikte yeni dersin kaydını yapar, eğer tüm bu işlemler sonrası başarılı bir şekilde kayıt
    olmuşsa true olmamışsa false dönecek.
     */
    suspend fun createNewLesson(lesson: TeacherLesson, completeEvent: (Boolean) -> Unit) {
        Firebase.database.reference.child("lessons")
            .child(System.currentTimeMillis().toString()).setValue(lesson)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    completeEvent(true)
                } else {
                    completeEvent(false)
                }
            }
    }

    /*
    Bu fonksiyon sadece öğretmen adını getirmek için kullanılıyor, buradan alınan veriyide çeşitli
    kısımlarda kullanabiliyoruz, ders oluştur derse öğrenci ekle vs.
     */

    suspend fun getTeacherName(completeEvent: (String, String) -> Unit) {
        Firebase.database.reference.child("teacher")
            .child(FirebaseAuth.getInstance().currentUser!!.email!!.toUserName()).get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    it.result!!.getValue(Teacher::class.java)!!.teacherName?.let { teacherName ->
                        completeEvent(
                            teacherName,
                            it.result!!.getValue(Teacher::class.java)!!.teacherMail!!
                        )
                    }
                }
            }
    }

    /*
    Öğrenciyi derse kaydederken öncelikle giriş yapacak öğrencinin bilgilerini almak gerekiyor,
    bu fonksiyonda yapılan tek işlem öğrencci bilgilerini almak ve bu bilgiler ile aşağıda olan,
    addStudentToLesson fonksiyonunu çağırmaktır.
     */
    fun getStudentAndAddLesson(uuid: String, completeEvent: (Boolean) -> Unit) {
        Firebase.database.reference.child("student")
            .child(FirebaseAuth.getInstance().currentUser!!.email!!.toUserName()).get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    it.result?.getValue(Student::class.java)?.let { student ->
                        addStudentToLesson(uuid, student, completeEvent)
                    }
                }
            }
    }

    /*
    Buradaki fonksiyonun tek görevi öğretmen giriş yaptığında ekranda eğer varsa öğretmenin oluşturduğu
    dersleri listeleyebilmek için veritabanındaki öğretmene dair tüm verileri çekmek.
     */
    suspend fun getTeacherLessons(completeEvent: (List<TeacherLesson>) -> Unit) {
        Firebase.database.reference.child("lessons")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    println("Brekpoint")
                }

                override fun onDataChange(p0: DataSnapshot) {
                    val lessons = mutableListOf<TeacherLesson>()
                    p0.children.forEach {
                        /*if (it.child("listOfStudents").value == null) {
                            val item: TeacherLesson? = it.getValue(TeacherLesson::class.java)
                            item?.let { lesson ->
                                val teacherName =
                                    lesson.listOfLessons?.firstOrNull { teacher -> teacher.teacher?.teacherName?.isNotEmpty() == true }
                                if (teacherName?.teacher?.teacherMail == FirebaseAuth.getInstance().currentUser!!.email!!) {
                                    lessons.add(lesson)
                                }
                            }
                        }else {*/
                        val listOfLessons = mutableListOf<Lesson>()
                        it.child("listOfLessons").children.forEach { mLesson ->
                            val listOfStudents = mutableListOf<Student>()
                            mLesson.child("listOfStudents").children.forEach { mListOfStudents ->
                                val item: Student? = mListOfStudents.getValue(Student::class.java)
                                item?.let { student ->
                                    listOfStudents.add(student)
                                }
                            }
                            val lessonUUID =
                                mLesson.child("lessonUUID").getValue(String::class.java)
                            val lessonName =
                                mLesson.child("lessonName").getValue(String::class.java)
                            val teacher = mLesson.child("teacher").getValue(Teacher::class.java)
                            val isOnline =
                                mLesson.child("lessonOnline").getValue(Boolean::class.java)
                            val lesson = Lesson(
                                listOfStudents = listOfStudents,
                                lessonUUID = lessonUUID,
                                lessonName = lessonName,
                                isLessonOnline = isOnline,
                                teacher = teacher
                            )
                            listOfLessons.add(lesson)
                        }
                        val teacherName = it.child("lessonName").getValue(String::class.java)
                        val teacherKey = it.key
                        val teacherLesson = TeacherLesson(
                            teacherKey = teacherKey,
                            lessonName = teacherName,
                            listOfLessons = listOfLessons
                        )
                        lessons.add(teacherLesson)
                        //}
                    }
                    completeEvent(lessons.asReversed())
                }
            })
    }

    /*
    Fonksiyonun göreve adından da anlaşıldığı gibi dersi silmeye yarıyor ancak şuanda kullanılmamakta
    çünkü dersi oluşturduktan sonra öğretmenlerin dersi silmelereni izin vermemeliyiz bu çeşitli
    sorunlara sebebiyet verebilir.
     */
    suspend fun removeLesson(uuid: String, completeEvent: (Boolean) -> Unit) {
        Firebase.database.reference.child("lessons")
            .get().addOnCompleteListener {
                if (it.isSuccessful) {
                    it.result!!.children.forEach {
                        if (it.getValue(Lesson::class.java)!!.lessonUUID == uuid) {
                            it.ref.removeValue()
                            completeEvent(true)
                        }
                    }
                } else {
                    completeEvent(false)
                }
            }
    }

    /*
    Buradaki fonksiyon dersteki haftaya örneğin 5.hafta, giriş yapmış öğrencilerin listesini getiriyor.
    Getirilen bu listeyi öğretmen sayfasından herhangi bir derse tıkladığımızda ekranda çıkan öğrenci listesidir.
     */
    suspend fun getListOfStudents(uuid: String, completeEvent: (List<Student>) -> Unit) {
        Firebase.database.reference.child("lessons").get().addOnCompleteListener {
            if (it.isSuccessful) {
                val listOfStudents: MutableList<Student> = mutableListOf()
                it.result.children.forEach { p0 ->
                    var mLesson = Lesson()
                    p0.child("listOfLessons").children.forEach { lLesson ->
                        if (lLesson.child("lessonUUID").getValue(String::class.java) == uuid) {
                            lLesson.child("listOfStudents").children.forEach { mStudent ->
                                listOfStudents.add(
                                    Student(
                                        studentMail = mStudent.child("studentMail")
                                            .getValue(String::class.java),
                                        studentName = mStudent.child("studentName")
                                            .getValue(String::class.java),
                                        studentNumber = mStudent.child("studentNumber")
                                            .getValue(String::class.java)
                                    )
                                )
                            }
                        }
                    }
                    completeEvent.invoke(listOfStudents)
                }
            }
        }
    }

    /*
    Bu fonksiyon ise öğrenci ekranında katıldığı dersleri ve haftaları ekranda göstermek için gerekli verileri çekip
    dönmeye yaramaktadır.
     */
    suspend fun listOfLessonsStudentAttempted(
        lesson: Lesson,
        onComplete: (List<TeacherLesson>) -> Unit
    ) {
        Firebase.database.reference.child("lessons")
            .addValueEventListener(object : ValueEventListener {

                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    val listOfLessons = mutableListOf<Lesson>()
                    val listOfStudents = mutableListOf<Student>()
                    p0.children.forEach { mLesson ->
                        mLesson.child("listOfLessons").children.forEach { week ->
                            week.child("listOfStudents").children.forEach { student ->
                                val item: Student? = student.getValue(Student::class.java)
                                if (item?.studentMail == FirebaseAuth.getInstance().currentUser!!.email!!) {
                                    val lessonUUID =
                                        week.child("lessonUUID").getValue(String::class.java)
                                    val lessonName =
                                        week.child("lessonName").getValue(String::class.java)
                                    val teacher =
                                        week.child("teacher").getValue(Teacher::class.java)
                                    val lessonWeek =
                                        week.child("lessonWeek").getValue(String::class.java)
                                    val tempLesson = week.child("listOfStudents").children.map {
                                        val mItem: Student = week.getValue(Student::class.java)!!
                                        mItem
                                    }.toList().let { students ->
                                        Lesson(
                                            listOfStudents = students,
                                            lessonUUID = lessonUUID,
                                            lessonName = lessonName,
                                            teacher = teacher,
                                            lessonWeek = lessonWeek
                                        )
                                    }
                                    tempLesson.let { tLesson -> listOfLessons.add(tLesson) }
                                }
                            }
                        }
                        val res = listOfLessons.groupBy { it.lessonName }
                            .map { TeacherLesson(lessonName = it.key,listOfLessons = it.value) }
                        onComplete(res)
                    }
                }
            })
    }

    /*
    Fonksiyon öğrencinin katıldığı dersten kaç hafta devamsızlığı kaldığı bilgisini hesaplayıp
    dönmektedir. Eğer ders daha önceden aktifleşti ve öğrenci katılmadıysa devamsızlık bir artar.
     */
    suspend fun getUsersAllLesson(
        lessonName: String,
        totalNonattendance: (Int) -> Unit
    ) {
        var lessonsActivated = 0
        var lessonAttended = 0
        Firebase.database.reference.child("lessons").get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    it.result.children.forEach { mLesson ->
                        if (lessonName == mLesson.child("lessonName")
                                .getValue(String::class.java)
                        ) {
                            mLesson.child("listOfLessons").children.forEach { week ->
                                if (week.child("lessonActivated")
                                        .getValue(Boolean::class.java) == true
                                ) {
                                    lessonsActivated++
                                    week.child("listOfStudents").children.forEach { student ->
                                        if (student.child("studentMail")
                                                .getValue(String::class.java) == FirebaseAuth.getInstance().currentUser?.email
                                        ) {
                                            lessonAttended++
                                        }
                                    }
                                }
                            }
                            totalNonattendance.invoke(lessonsActivated - lessonAttended)
                            lessonAttended = 0
                            lessonsActivated = 0
                        }
                    }
                }
            }
    }

    /*
    Yukarıda daha önceden belirtilen öğrenci bilgisini getirme ve derse ekleme fonksiyonuun devamı. Alınan öğrenci
    bilgisini yine parametre olarak verilen ilgili derse kaydını sağlayan fonksiyondur.
     */
    private fun addStudentToLesson(
        uuid: String,
        student: Student,
        completeEvent: (Boolean) -> Unit
    ) {
        Firebase.database.reference.child("lessons")
            .get().addOnCompleteListener {
                var isSuccess = false
                if (it.isSuccessful) {
                    it.result!!.children.forEach { mLesson ->
                        mLesson.child("listOfLessons").children.forEach { week ->
                            if (week.child("lessonUUID").getValue(String::class.java) == uuid) {
                                isSuccess = true
                                student.studentNumber?.let { studentNumber ->
                                    week.ref.child("listOfStudents").child(studentNumber)
                                        .setValue(student)
                                }
                                completeEvent(true)
                            }
                        }
                    }
                    if (!isSuccess) {
                        completeEvent(false)
                    }
                }

            }
    }

    /*
    Dersin aktiflik durumunu değiştirir. eğer ders aktif ise kapatır, kapalı ise aktifleştirir.
     */
    fun setIsCheckedStatus(lesson: Lesson, checked: Boolean, position: Int) {
        Firebase.database.reference.child("lessons")
            .get().addOnCompleteListener {
                if (it.isSuccessful) {
                    it.result.children.forEach { mLesson ->
                        if (mLesson.child("lessonName")
                                .getValue(String::class.java) == lesson.lessonName
                        ) {
                            mLesson.child("listOfLessons").children.forEach { week ->
                                if (week.child("lessonUUID")
                                        .getValue(String::class.java) == lesson.lessonUUID
                                ) {
                                    week.ref.child("lessonOnline").setValue(checked)
                                    week.ref.child("lessonActivated").setValue(true)
                                }
                            }
                        }
                    }
                }
            }
    }

    /*
    Veritabanında bulunan dersin id,sini değiştirir yani QR kodunu değiştirmeyi sağlar,
     */
    suspend fun changeQrParameter(
        lesson: Lesson,
        randomUUID: String,
        onComplete: (Boolean) -> Unit
    ) {
        Firebase.database.reference.child("lessons")
            .get().addOnCompleteListener {
                if (it.isSuccessful) {
                    it.result.children.forEach { mLesson ->
                        if (mLesson.child("lessonName")
                                .getValue(String::class.java) == lesson.lessonName
                        ) {
                            mLesson.child("listOfLessons").children.forEach { week ->

                                val lessonUUID =
                                    week.child("lessonUUID").getValue(String::class.java)
                                if (lessonUUID == lesson.lessonUUID)
                                    week.ref.child("lessonUUID").setValue(randomUUID)
                                        .addOnCompleteListener { result ->
                                            if (result.isSuccessful) {
                                                onComplete.invoke(true)
                                            } else {
                                                onComplete.invoke(false)
                                            }
                                        }
                            }
                        }
                    }
                }
            }
    }

    /*
    Buradaki fonsiyon RepotData sınıfımızın içerisinde bulunan her bir değer için veritabanından verileri
    alıp ReportData objesini oluşturur. Bu objede derse ait öğrenci isimi ve hangi haftalar katıldığının verisi
    bulunmaktadır.
     */
    suspend fun createLessonReport(
        lessonName: String
    ) = suspendCoroutine<List<ReportData>> { contination ->
        val weeks = mutableListOf<String>()
        val listOfReport = mutableListOf<ReportData>()
        var counter = 0
        Firebase.database.reference.child("lessons").child(lessonName).get().addOnCompleteListener { lesson ->
            if (lesson.isSuccessful) {
                Firebase.database.reference.child("student").get()
                    .addOnCompleteListener { students ->
                        if (students.isSuccessful) {
                            students.result.children.forEach { mStudent ->
                                lesson.result.child("listOfLessons").children.forEach { week ->
                                    var isStudentAttended = false
                                    week.child("listOfStudents").children.forEach { lessonStudent ->
                                        if (mStudent.child("studentMail")
                                                .getValue(String::class.java) == lessonStudent.child(
                                                "studentMail"
                                            ).getValue(String::class.java)
                                        ) {
                                            isStudentAttended = true
                                        }
                                    }
                                    val isLessonActivated =
                                        week.child("lessonActivated").getValue(Boolean::class.java)
                                    if (isLessonActivated == true && isStudentAttended) {
                                        weeks.add("+")
                                        isStudentAttended = false
                                    } else if (isLessonActivated == true) {
                                        weeks.add("X")
                                    } else {
                                        weeks.add("—")
                                    }
                                    counter++
                                }
                                listOfReport.add(
                                    ReportData(
                                        studentName = mStudent.child("studentName")
                                            .getValue(String::class.java),
                                        week = weeks.toList()
                                    )
                                )
                                weeks.clear()
                            }
                            contination.resume(listOfReport)
                        }
                    }
            }
        }
    }

}
