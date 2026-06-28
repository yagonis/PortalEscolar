const API_URL = "http://localhost:8080";

export async function listarNoticias() {
    const resposta = await fetch(`${API_URL}/noticias`);
    return resposta.json();
}