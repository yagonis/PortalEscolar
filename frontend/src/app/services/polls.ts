const API_URL = "http://localhost:8080";

export type PollOption = {
  id: string;
  text: string;
  displayOrder: number;
};

export type Poll = {
  id: string;
  question: string;
  description?: string;
  status: string;
  opensAt?: string;
  closesAt?: string;
  allowMultipleVotes: boolean;
  createdAt: string;
  options: PollOption[];
};

type PollPage = {
  content: Poll[];
  totalElements: number;
  totalPages: number;
};

function getToken() {
  return localStorage.getItem("token");
}

export async function listarEnquetes(): Promise<PollPage> {
  const resposta = await fetch(`${API_URL}/api/polls`);

  if (!resposta.ok) {
    throw new Error("Erro ao listar enquetes.");
  }

  return resposta.json();
}

export async function listarEnquetesAbertas(): Promise<Poll[]> {
  const dados = await listarEnquetes();

  return dados.content.filter(
    (enquete) =>
      enquete.status === "OPEN" ||
      enquete.status === "ACTIVE"
  );
}

export async function votarEnquete(
  pollId: string,
  optionId: string
): Promise<void> {
  const token = getToken();

  if (!token) {
    throw new Error("AUTH_REQUIRED");
  }

  const resposta = await fetch(
    `${API_URL}/api/polls/${pollId}/vote`,
    {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify({
        optionId,
      }),
    }
  );

  if (!resposta.ok) {
    const erro = await resposta.json().catch(() => null);

    throw new Error(
      erro?.message || "Não foi possível registrar o voto."
    );
  }
}