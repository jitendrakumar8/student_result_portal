function loadSectionsForRank(className) {

    const sectionSelect = document.getElementById("sectionSelect");

    // reset section dropdown
    sectionSelect.innerHTML =
        '<option value="">Select Section</option>';

    if (!className) return;

    fetch('/admin/sections-by-class?className=' + className)
        .then(res => res.json())
        .then(sections => {
            sections.forEach(section => {
                const option = document.createElement("option");
                option.value = section;
                option.textContent = section;
                sectionSelect.appendChild(option);
            });
        });
}
