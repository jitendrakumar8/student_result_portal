function loadSections(className) {

    const sectionSelect = document.getElementById("sectionSelect");

    // reset
    sectionSelect.innerHTML =
        '<option value="">Select Section</option>';

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
        })
        .catch(err => console.error("Section load error", err));
}
