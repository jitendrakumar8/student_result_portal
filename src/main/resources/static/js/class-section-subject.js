document.addEventListener("DOMContentLoaded", () => {

    const classSelect = document.getElementById("classSelect");
    const sectionSelect = document.getElementById("sectionSelect");
    const subjectSelect = document.getElementById("subjectSelect");

    if (!classSelect || !sectionSelect || !subjectSelect) return;

    // CLASS → SECTION
    classSelect.addEventListener("change", () => {
        const className = classSelect.value;

        sectionSelect.innerHTML = '<option value="">Select Section</option>';
        subjectSelect.innerHTML = '<option value="">Select Subject</option>';

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

    // SECTION → SUBJECT
    sectionSelect.addEventListener("change", () => {
        const className = classSelect.value;
        const section = sectionSelect.value;

        subjectSelect.innerHTML = '<option value="">Select Subject</option>';

        if (!className || !section) return;

        fetch(`/admin/subjects-by-class-section?className=${className}&section=${section}`)
            .then(res => res.json())
            .then(subjects => {
                subjects.forEach(sub => {
                    const opt = document.createElement("option");
                    opt.value = sub;
                    opt.textContent = sub;
                    subjectSelect.appendChild(opt);
                });
            });
    });

});
