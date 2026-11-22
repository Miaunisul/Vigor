const botaoMostrarSenha = document.querySelector("#mostrar-senha");
const campoSenha = document.querySelector("#senha");

botaoMostrarSenha?.addEventListener("click", () => {
    const mostrando = campoSenha.type === "password";
    campoSenha.type = mostrando ? "text" : "password";
    botaoMostrarSenha.textContent = mostrando ? "Ocultar" : "Mostrar";
    botaoMostrarSenha.setAttribute("aria-pressed", String(mostrando));
});
