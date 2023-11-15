function showModalInformativo(message) {
    const modalInformativo = document.createElement("div");
    modalInformativo.classList.add("modal", "fade");
    modalInformativo.setAttribute("tabindex", "-1");
    modalInformativo.id = "modalInformativo";
    modalInformativo.innerHTML = `
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Información</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar"></button>
            </div>
            <div class="modal-body">
                <p>${message}</p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
            </div>
        </div>
    </div>`;

    document.body.appendChild(modalInformativo);

    const modal = new bootstrap.Modal(modalInformativo);
    modal.show();

    modalInformativo.addEventListener("hidden.bs.modal", () => {
        document.body.removeChild(modalInformativo);
    });
}
