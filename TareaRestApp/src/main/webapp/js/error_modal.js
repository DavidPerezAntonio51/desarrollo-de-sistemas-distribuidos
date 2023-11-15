function showErrorModal(message) {
    const errorModal = document.createElement("div");
    errorModal.classList.add("modal", "fade");
    errorModal.setAttribute("tabindex", "-1");
    errorModal.innerHTML = `
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title">Error</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar"></button>
        </div>
        <div class="modal-body">
          ${message}
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
        </div>
      </div>
    </div>`;
    document.body.appendChild(errorModal);
    const modal = new bootstrap.Modal(errorModal);
    modal.show();
    errorModal.addEventListener("hidden.bs.modal", () => {
        document.body.removeChild(errorModal);
    });
}
