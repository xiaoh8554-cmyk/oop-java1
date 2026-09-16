# APU Medical Centre - Hospital Management System

Java Swing desktop HMS using `.txt` files for persistent data. Open the **HospitalManagementSystem** folder itself in Visual Studio Code so the working directory contains `Main.java` and the `data` folder.

## Requirements covered

1. Login access with role-based dashboards.
2. Patient self-registration + Administrative Staff user creation for every role.
3. Medical grading and billing.
4. Create wards / clinics.
5. Create departments / specialties.
6. Design assessment / check-up types.
7. Doctors key in medical assessments and lab results.
8. Doctors provide clinical feedback and prescriptions; patients can submit care feedback.
9. Medical Managers generate analytical reports.

## Role accounts for testing

- Administrative Staff: `admin` / `admin123`
- Medical Manager: `manager` / `manager123`
- Doctor: `doctor` / `doctor123`
- Patient: `patient` / `patient123`

## Run in VS Code

1. Install **Extension Pack for Java** by Microsoft.
2. Open this folder in VS Code: `HospitalManagementSystem`.
3. Open `Main.java`.
4. Click **Run** above `main`, or press `F5` and select **Run HMS**.
5. Log in with one of the test accounts above.

## Run in Terminal

### Windows PowerShell

```powershell
$files = Get-ChildItem -Recurse -Filter *.java | ForEach-Object { $_.FullName }
javac -d out $files
java -cp out Main
```

### macOS / Linux

```bash
mkdir -p out
javac -d out $(find . -name "*.java")
java -cp out Main
```

## Important workflow to test

1. Login as Medical Manager and create an assessment type if needed.
2. Login as Doctor. Open **Assessment & Lab Results**, use Patient ID `P001`, choose a check-up type, enter result/lab result, and save.
3. The system automatically creates an **UNPAID** bill based on that assessment type's fee.
4. Login as Medical Manager and open **Medical Grading** to classify the assessment.
5. Login as Patient and open **My Medical Records** and **My Billing**. The same assessment and bill are visible.
6. Doctor can issue a prescription and clinical feedback. Patient can see both and submit patient feedback.
7. Medical Manager can open **Analytical Reports** to see updated totals and grade/billing statistics.

## Data format

All `.txt` files use `|` as the separator. For example:

`users.txt`

```text
A001|admin|admin123|ADMINISTRATIVE_STAFF|System Administrator|admin@apu.local
```

This is an educational assignment architecture. Passwords are stored as plain text only to keep the file-handling logic easy to understand. A production hospital system must use secure authentication, password hashing, access controls, audit logging, database transactions, encryption, privacy controls and regulatory safeguards.


## Navigation update
Every role function window now includes a **Back to Dashboard** button. The dashboard stays open while a function window is being used, so Back closes the current function window and returns the user to the previous dashboard.
