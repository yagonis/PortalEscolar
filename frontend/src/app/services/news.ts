const API_URL = "http://localhost:8080";

function getToken() {
  return localStorage.getItem("token");
}

function headers() {
  return {
    "Content-Type": "application/json",
    Authorization: `Bearer ${getToken()}`,
  };
}

export async function listarNoticias() {
  const resposta = await fetch(`${API_URL}/api/news?isAdmin=true`, {
    headers: headers(),
  });

  if (!resposta.ok) {
    throw new Error("Erro ao listar notícias");
  }

  return resposta.json();
}

export async function criarNoticia(noticia: any) {
  const resposta = await fetch(`${API_URL}/api/news`, {
    method: "POST",
    headers: headers(),
    body: JSON.stringify(noticia),
  });

  if (!resposta.ok) {
    throw new Error("Erro ao criar notícia");
  }

  return resposta.json();
}

export async function editarNoticia(id: string, noticia: any) {
  const resposta = await fetch(`${API_URL}/api/news/${id}`, {
    method: "PUT",
    headers: headers(),
    body: JSON.stringify(noticia),
  });

  if (!resposta.ok) {
    throw new Error("Erro ao editar notícia");
  }

  return resposta.json();
}

export async function excluirNoticia(id: string) {
  const resposta = await fetch(`${API_URL}/api/news/${id}`, {
    method: "DELETE",
    headers: headers(),
  });

  if (!resposta.ok) {
    throw new Error("Erro ao excluir notícia");
  }
}

export async function publicarNoticia(id: string) {
  const resposta = await fetch(`${API_URL}/api/news/${id}/publish`, {
    method: "PATCH",
    headers: headers(),
  });

  if (!resposta.ok) {
    throw new Error("Erro ao publicar notícia");
  }

  return resposta.json();
}

export async function arquivarNoticia(id: string) {
  const resposta = await fetch(`${API_URL}/api/news/${id}/archive`, {
    method: "PATCH",
    headers: headers(),
  });

  if (!resposta.ok) {
    throw new Error("Erro ao arquivar notícia");
  }

  return resposta.json();
}

export async function voltarParaRascunho(id: string) {
  const resposta = await fetch(`${API_URL}/api/news/${id}/draft`, {
    method: "PATCH",
    headers: headers(),
  });

  if (!resposta.ok) {
    throw new Error("Erro ao voltar notícia para rascunho");
  }

  return resposta.json();
}

export async function listarNoticiasPublicas() {
  const resposta = await fetch(`${API_URL}/api/news`, {
    headers: {
      "Content-Type": "application/json",
    },
  });

  if (!resposta.ok) {
    throw new Error("Erro ao listar notícias públicas");
  }

  return resposta.json();
}