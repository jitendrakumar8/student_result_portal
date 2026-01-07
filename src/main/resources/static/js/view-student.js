const classSelect = document.getElementById("classSelect");
const sectionSelect = document.getElementById("sectionSelect");
const tableBody = document.getElementById("studentTable");

// LOAD SECTIONS WHEN CLASS CHANGES
classSelect.addEventListener("change", function () {
    const className = this.value;

    sectionSelect.innerHTML = '<option value="">Select Section</option>';
    tableBody.innerHTML =
        '<tr><td colspan="9">Select Section</td></tr>';

    if (!className) return;

    fetch(`/admin/get-sections?className=${className}`)
        .then(res => res.json())
        .then(data => {
            data.forEach(sec => {
                sectionSelect.innerHTML +=
                    `<option value="${sec}">${sec}</option>`;
            });
        });
});

// LOAD STUDENTS WHEN SECTION CHANGES
sectionSelect.addEventListener("change", function () {
    const section = this.value;
    const className = classSelect.value;

    tableBody.innerHTML = "";

    if (!section) return;

    fetch(`/admin/get-students?className=${className}&section=${section}`)
        .then(res => res.json())
        .then(data => {

            if (data.length === 0) {
                tableBody.innerHTML =
                    `<tr><td colspan="9">No Students Found</td></tr>`;
                return;
            }

            data.forEach(stu => {
                tableBody.innerHTML += `
                    <tr>
                        <td>${stu.rollNumber}</td>
                        <td>${stu.name}</td>
                        <td>${stu.className}</td>
                        <td>${stu.section}</td>
                        <td>${stu.fatherName}</td>
                        <td>${stu.gender}</td>
                        <td>${stu.caste}</td>
                        <td>${stu.religion}</td>
                        <td>
                         <a href="/admin/student-profile/${stu.id}" class="btn btn-info btn-sm"> View </a>
                         <a href="/admin/edit-student/${stu.id}" class="btn btn-warning btn-sm"> Edit  </a>
                         <button class="btn btn-danger btn-sm"
                                 onclick="deleteStudent(${stu.id})">
                             Delete
                         </button>

                        </td>

                    </tr>
                `;
            });
        });
});
function deleteStudent(id) {

    if (!confirm("Are you sure you want to delete this student?")) {
        return;
    }

    fetch(`/admin/delete-student/${id}`, {
        method: "POST"
    })
    .then(res => res.text())
    .then(result => {

        if (result === "success") {
            alert("Student deleted successfully");
            location.reload();   // 👈 refresh table
        }
    })
    .catch(err => {
        console.error(err);
        alert("Something went wrong");
    });
}

