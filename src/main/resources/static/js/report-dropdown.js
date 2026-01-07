document.addEventListener("DOMContentLoaded", () => {

    const classSelect = document.getElementById("classSelect");
    const sectionSelect = document.getElementById("sectionSelect");
    const studentSelect = document.getElementById("studentSelect");

    classSelect.addEventListener("change", () => {
        const className = classSelect.value;

        sectionSelect.innerHTML = '<option value="">Select Section</option>';
        studentSelect.innerHTML = '<option value="">Select Student</option>';

        if (!className) return;

        fetch(`/admin/sections-by-class?className=${className}`)
            .then(res => res.json())
            .then(sections => {
                sections.forEach(sec => {
                    const opt = document.createElement("option");
                    opt.value = sec;
                    opt.textContent = sec;
                    sectionSelect.appendChild(opt);
                });
            });
    });

    sectionSelect.addEventListener("change", () => {
        const className = classSelect.value;
        const section = sectionSelect.value;

        studentSelect.innerHTML = '<option value="">Select Student</option>';

        if (!className || !section) return;

        fetch(`/admin/students-by-class-section?className=${className}&section=${section}`)
            .then(res => res.json())
            .then(students => {
                students.forEach(stu => {
                    const opt = document.createElement("option");
                    opt.value = stu.id;
                    opt.textContent = stu.rollNumber + " - " + stu.name;
                    studentSelect.appendChild(opt);
                });
            });
    });
});
