import { useEffect, useState } from "react";
import {
  Plus, Pencil, Trash2, Eye, EyeOff, Search,
  Newspaper, LogOut, LayoutDashboard, Bell, Calendar,
  X, Check, ImageIcon, AlignLeft, Link2
} from "lucide-react";
import { useNavigate } from "react-router";
import { Card, CardContent } from "../components/ui/card";
import { Button } from "../components/ui/button";
import { Input } from "../components/ui/input";
import { Textarea } from "../components/ui/textarea";
import { Label } from "../components/ui/label";
import { Separator } from "../components/ui/separator";
import {Link} from "react-router";
import {
  criarNoticia,
  listarNoticias,
  editarNoticia,
  excluirNoticia,
  publicarNoticia,
  voltarParaRascunho,
  arquivarNoticia
} from "../services/news";

type Status = "PUBLISHED" | "DRAFT" | "ARCHIVED";

interface Noticia {
  id: string;
  title: string;
  subtitle: string | null;
  body: string;
  imageUrl: string | null;
  status: Status;
  publishedAt: string | null;
  updatedAt: string | null;
}

type NoticiaForm = {
  title: string;
  subtitle: string;
  body: string;
  imageUrl: string;
};

type Modo = "lista" | "novo" | "editar";

const noticia_vazia: NoticiaForm = {
  title: "",
  subtitle: "",
  body: "",
  imageUrl: "",
};

export function AdminPage() {
  const navigate = useNavigate();

  const [noticias, setNoticias] = useState<Noticia[]>([]);
  const [modo, setModo] = useState<Modo>("lista");
  const [noticiaEditando, setNoticiaEditando] = useState<Noticia | null>(null);
  const [form, setForm] = useState<NoticiaForm>(noticia_vazia);
  const [busca, setBusca] = useState("");

  async function carregarNoticias() {
    try {
      const dados = await listarNoticias();
      setNoticias(dados.content ?? []);
    } catch (erro) {
      console.error("Erro ao carregar notícias:", erro);
    }
  }

  useEffect(() => {
    carregarNoticias();
  }, []);

  const noticiasFiltradas = noticias.filter((n) =>
    n.title.toLowerCase().includes(busca.toLowerCase())
  );

  const publicadas = noticias.filter((n) => n.status === "PUBLISHED").length;
  const rascunhos = noticias.filter((n) => n.status === "DRAFT").length;

  function abrirNovo() {
    setForm(noticia_vazia);
    setNoticiaEditando(null);
    setModo("novo");
  }

  function abrirEditar(noticia: Noticia) {
    setForm({
      title: noticia.title,
      subtitle: noticia.subtitle ?? "",
      body: noticia.body,
      imageUrl: noticia.imageUrl ?? noticia_vazia.imageUrl,
    });

    setNoticiaEditando(noticia);
    setModo("editar");
  }

  function cancelar() {
    setModo("lista");
    setNoticiaEditando(null);
    setForm(noticia_vazia);
  }

  async function salvar() {
    if (!form.title.trim() || !form.body.trim()) {
      alert("Preencha o título e o conteúdo.");
      return;
    }

    try {
      if (modo === "novo") {
        await criarNoticia(form);
      } else if (noticiaEditando) {
        await editarNoticia(noticiaEditando.id, form);
      }

      await carregarNoticias();
      cancelar();
    } catch (erro) {
      console.error(erro);
      alert("Erro ao salvar notícia.");
    }
  }

  async function excluir(id: string) {
    if (!window.confirm("Deseja realmente excluir esta notícia?")) return;

    try {
      await excluirNoticia(id);
      await carregarNoticias();
    } catch (erro) {
      console.error(erro);
      alert("Erro ao excluir notícia.");
    }
  }

  async function togglePublicada(noticia: Noticia) {
    try {
      if (noticia.status === "DRAFT") {
        await publicarNoticia(noticia.id);
      } else if(noticia.status === "PUBLISHED") {
        await arquivarNoticia(noticia.id);
      } else if (noticia.status === "ARCHIVED") {
        await voltarParaRascunho(noticia.id);
      }

      await carregarNoticias();
    } catch (erro) {
      console.error(erro);
      alert("Erro ao alterar status da notícia.");
    }
  }

  function logout() {
    localStorage.removeItem("token");
    localStorage.removeItem("user");
    navigate("/login");
  }

  const formulario = (
    <div className="max-w-3xl mx-auto space-y-6">
      <div className="flex items-center justify-between">
        <h2 className="text-xl font-semibold">
          {modo === "novo" ? "Nova Notícia" : "Editar Notícia"}
        </h2>

        <Button variant="ghost" size="icon" onClick={cancelar}>
          <X className="size-5" />
        </Button>
      </div>

      <Card>
        <CardContent className="pt-6 space-y-5">
          <div className="space-y-2">
            <Label htmlFor="imageUrl" className="flex items-center gap-2">
              <ImageIcon className="size-4 text-muted-foreground" />
              Imagem de Capa
            </Label>

            <div className="relative">
              <Link2 className="absolute left-3 top-1/2 -translate-y-1/2 size-4 text-muted-foreground" />
              <Input
                id="imageUrl"
                placeholder="https://exemplo.com/imagem.jpg"
                className="pl-9"
                value={form.imageUrl}
                onChange={(e) => setForm((f) => ({ ...f, imageUrl: e.target.value }))}
              />
            </div>

            {form.imageUrl && (
              <div className="mt-2 size-24 rounded-lg overflow-hidden border bg-muted">
                <img
                  src={form.imageUrl}
                  alt="Pré-visualização"
                  className="size-full object-cover"
                  onError={(e) => {
                    (e.target as HTMLImageElement).style.display = "none";
                  }}
                />
              </div>
            )}
          </div>

          <Separator />

          <div className="space-y-2">
            <Label htmlFor="title">Título *</Label>
            <Input
              id="title"
              placeholder="Título da notícia"
              value={form.title}
              onChange={(e) => setForm((f) => ({ ...f, title: e.target.value }))}
            />
          </div>

          <div className="space-y-2">
            <Label htmlFor="subtitle" className="flex items-center gap-2">
              <AlignLeft className="size-4 text-muted-foreground" />
              Subtítulo
            </Label>
            <Textarea
              id="subtitle"
              placeholder="Breve descrição exibida na listagem"
              rows={2}
              className="resize-none"
              value={form.subtitle}
              onChange={(e) => setForm((f) => ({ ...f, subtitle: e.target.value }))}
            />
          </div>

          <div className="space-y-2">
            <Label htmlFor="body">Conteúdo Completo *</Label>
            <Textarea
              id="body"
              placeholder="Texto completo da notícia..."
              rows={6}
              className="resize-none"
              value={form.body}
              onChange={(e) => setForm((f) => ({ ...f, body: e.target.value }))}
            />
          </div>
        </CardContent>
      </Card>

      <div className="flex gap-3 justify-end">
        <Button variant="outline" onClick={cancelar}>
          Cancelar
        </Button>

        <Button
          onClick={salvar}
          disabled={!form.title.trim() || !form.body.trim()}
          className="gap-2"
        >
          <Check className="size-4" />
          {modo === "novo" ? "Salvar Notícia" : "Salvar Alterações"}
        </Button>
      </div>
    </div>
  );

  return (
    <div className="min-h-screen bg-muted/30">
      <div className="flex">
        <aside className="hidden lg:flex flex-col w-56 min-h-[calc(100vh-57px)] bg-card border-r shrink-0">
          <nav className="flex-1 p-4 space-y-1">
            <p className="text-xs font-medium text-muted-foreground uppercase tracking-wider px-2 mb-3">
              Conteúdo
            </p>

            <button
              onClick={() => setModo("lista")}
              className={`w-full flex items-center gap-3 px-3 py-2 rounded-lg text-sm transition-colors ${modo === "lista"
                  ? "bg-primary text-primary-foreground"
                  : "hover:bg-accent text-foreground"
                }`}
            >
              <Newspaper className="size-4" />
              Notícias
            </button>


            <button className={`w-full flex items-center gap-3 px-3 py-2 rounded-lg text-sm transition-colors ${modo === "lista"
                  ? "bg-primary text-primary-foreground"
                  : "hover:bg-accent text-foreground"
                }`}
            >
              <Calendar className="size-4" />
              <Link to="/vote"> Enquetes </Link>
            </button>
            <button className="w-full flex items-center gap-3 px-3 py-2 rounded-lg text-sm text-muted-foreground hover:bg-accent transition-colors opacity-50 cursor-not-allowed">
              <Bell className="size-4" />
              Avisos
            </button>
          </nav>

          <div className="p-4 border-t">
            <Button
              variant="ghost"
              size="sm"
              className="w-full justify-start gap-2 text-muted-foreground"
              onClick={logout}
            >
              <LogOut className="size-4" />
              Sair do painel
            </Button>
          </div>
        </aside>

        <div className="flex-1 p-6 lg:p-8">
          {modo === "lista" ? (
            <div className="space-y-6">
              <div className="flex items-center justify-between">
                <div>
                  <h1 className="text-2xl font-bold flex items-center gap-2">
                    <LayoutDashboard className="size-6 text-primary" />
                    Painel Administrativo
                  </h1>

                  <p className="text-muted-foreground text-sm mt-1">
                    Gerencie as notícias publicadas no portal escolar
                  </p>
                </div>

                <Button onClick={abrirNovo} className="gap-2">
                  <Plus className="size-4" />
                  Nova Notícia
                </Button>
              </div>

              <div className="grid grid-cols-2 gap-4">
                <Card>
                  <CardContent className="pt-5 pb-4">
                    <p className="text-xs text-muted-foreground uppercase tracking-wider">
                      Total
                    </p>
                    <p className="text-3xl font-bold mt-1">{noticias.length}</p>
                    <p className="text-xs text-muted-foreground mt-1">
                      notícias cadastradas
                    </p>
                  </CardContent>
                </Card>

                <Card>
                  <CardContent className="pt-5 pb-4">
                    <p className="text-xs text-muted-foreground uppercase tracking-wider">
                      Publicadas
                    </p>
                    <p className="text-3xl font-bold mt-1 text-green-600">
                      {publicadas}
                    </p>
                    <p className="text-xs text-muted-foreground mt-1">
                      {rascunhos} em rascunho
                    </p>
                  </CardContent>
                </Card>
              </div>

              <div className="flex gap-3 flex-wrap">
                <div className="relative flex-1 min-w-48">
                  <Search className="absolute left-3 top-1/2 -translate-y-1/2 size-4 text-muted-foreground" />
                  <Input
                    placeholder="Buscar notícias..."
                    className="pl-9"
                    value={busca}
                    onChange={(e) => setBusca(e.target.value)}
                  />
                </div>
              </div>

              <Card>
                <CardContent className="p-0">
                  {noticiasFiltradas.length === 0 ? (
                    <div className="py-16 text-center text-muted-foreground">
                      <Newspaper className="size-10 mx-auto mb-3 opacity-30" />
                      <p>Nenhuma notícia encontrada</p>
                    </div>
                  ) : (
                    <div className="divide-y">
                      {noticiasFiltradas.map((noticia) => (
                        <div
                          key={noticia.id}
                          className="flex items-center gap-4 p-4 hover:bg-muted/40 transition-colors"
                        >
                          <div className="size-12 rounded-lg overflow-hidden bg-muted shrink-0 flex items-center justify-center">
                            {noticia.imageUrl ? (
                              <img
                                src={noticia.imageUrl}
                                alt={noticia.title}
                                className="size-full object-cover"
                                onError={(e) => {
                                  (e.target as HTMLImageElement).style.display = "none";
                                }}
                              />
                            ) : (
                              <ImageIcon className="size-5 text-muted-foreground" />
                            )}
                          </div>

                          <div className="flex-1 min-w-0">
                            <div className="flex items-center gap-2 mb-0.5">
                              <p className="font-medium text-sm truncate">
                                {noticia.title}
                              </p>
                            </div>

                            <div className="flex items-center gap-3 text-xs text-muted-foreground">
                              <span>
                                {noticia.publishedAt ?? "Não publicada"}
                              </span>
                            </div>
                          </div>

                          <button
                            onClick={() => togglePublicada(noticia)}
                            className={`flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-medium transition-colors shrink-0 ${noticia.status === "PUBLISHED"
                                ? "bg-green-100 text-green-700 hover:bg-green-200"
                                : "bg-muted text-muted-foreground hover:bg-muted/80"
                              }`}
                          >
                            {noticia.status === "DRAFT" && (
                              <>
                                <Eye className="size-3" />
                                Publicar
                              </>
                            )}
                            {noticia.status === "PUBLISHED" && (
                              <>
                                <EyeOff className="size-3" />
                                Arquivar
                              </>
                            )}
                            {noticia.status === "ARCHIVED" && (
                              <>
                                <Eye className="size-3" />
                                Voltar para rascunho
                              </>
                            )}
                          </button>

                          <div className="flex items-center gap-1 shrink-0">
                            <Button
                              variant="ghost"
                              size="icon"
                              className="size-8"
                              title="Editar"
                              onClick={() => abrirEditar(noticia)}
                            >
                              <Pencil className="size-4" />
                            </Button>

                            <Button
                              variant="ghost"
                              size="icon"
                              className="size-8 text-destructive hover:text-destructive hover:bg-destructive/10"
                              title="Excluir"
                              onClick={() => excluir(noticia.id)}
                            >
                              <Trash2 className="size-4" />
                            </Button>
                          </div>
                        </div>
                      ))}
                    </div>
                  )}
                </CardContent>
              </Card>
            </div>
          ) : (
            formulario
          )}
        </div>
      </div>
    </div>
  );
}