0 - not, 1 - partly, 2- mostly finish
# gabai
* profile 0
* login 2
* signup 2
* manage synagoge 1
* massages 0

# prayer
* profile 0
* login 2
* signup 2
* find synagoge 0 
* fav synagoge 0
* report synagoge 0
* like synagoge 0
* massages 0 **hardest**

### cronolog
signup -> login -> profile -> manage synagoge -> fav/report/like synagoge -> find synagoge -> massages

### activitys 
IntroActivity 2
SignUpActivity 2
LoginActivity 2

ViewSynagogeActivity -> report\like\fav (recycleView prayers) 1
EditSynagogeActivity -> change name\lovation?\nosah/prayers 1

ProfileGabiActivity 1
-> ManageSynagogeActivity 1
-> MassagesActivity 1

ProfilePrayerActivity 1
-> findSynagogeActivity 1
-> FavoriteActivity 1
-> MassagesActivity (dup) 1 

