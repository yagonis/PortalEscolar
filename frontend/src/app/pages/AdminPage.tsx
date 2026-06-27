import { useState } from "react";
import {
  Plus, Pencil, Trash2, Eye, EyeOff, Search,
  Newspaper, LogOut, LayoutDashboard, Bell, Calendar,
  X, Check, ImageIcon, Tag, AlignLeft
} from "lucide-react";
import { Link } from "react-router";
import { Card, CardContent, CardHeader, CardTitle } from "../components/ui/card";
import { Button } from "../components/ui/button";
import { Badge } from "../components/ui/badge";
import { Input } from "../components/ui/input";
import { Textarea } from "../components/ui/textarea";
import { Label } from "../components/ui/label";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "../components/ui/select";
import { Separator } from "../components/ui/separator";

type Categoria = "Esportes" | "Sustentabilidade" | "Tecnologia" | "Cultura" | "Institucional" | "Saúde";

interface Noticia {
  id: number;
  titulo: string;
  resumo: string;
  conteudo: string;
  data: string;
  categoria: Categoria;
  publicada: boolean;
  destaque: boolean;
  imagem: string;
}

const CATEGORIAS: Categoria[] = [
  "Esportes", "Sustentabilidade", "Tecnologia", "Cultura", "Institucional", "Saúde"
];

const GRADIENTES: Record<string, string> = {
  "bg-gradient-to-br from-yellow-400 to-orange-500": "Amarelo → Laranja",
  "bg-gradient-to-br from-green-400 to-emerald-600": "Verde → Esmeralda",
  "bg-gradient-to-br from-blue-400 to-indigo-600": "Azul → Índigo",
  "bg-gradient-to-br from-pink-400 to-rose-600": "Rosa → Vermelho",
  "bg-gradient-to-br from-purple-400 to-violet-600": "Roxo → Violeta",
  "bg-gradient-to-br from-teal-400 to-cyan-600": "Teal → Ciano",
};

const noticias_iniciais: Noticia[] = [
  {
    id: 1,
    titulo: "Alunos do 5º ano conquistam medalhas na Olimpíada de Matemática",
    resumo: "Nossa escola teve destaque na competição regional com 8 medalhas conquistadas pelos estudantes.",
    conteudo: "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
    data: "25/05/2026",
    categoria: "Esportes",
    publicada: true,
    destaque: true,
    imagem: "bg-gradient-to-br from-yellow-400 to-orange-500",
  },
  {
    id: 2,
    titulo: "Projeto de Sustentabilidade é apresentado na comunidade",
    resumo: "Iniciativa dos alunos do ensino médio sobre reciclagem e preservação ambiental.",
    conteudo: "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
    data: "23/05/2026",
    categoria: "Sustentabilidade",
    publicada: true,
    destaque: false,
    imagem: "bg-gradient-to-br from-green-400 to-emerald-600",
  },
  {
    id: 3,
    titulo: "Nova biblioteca digital disponível para todos os alunos",
    resumo: "Acervo digital com mais de 5 mil títulos já pode ser acessado pelo portal.",
    conteudo: "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
    data: "20/05/2026",
    categoria: "Tecnologia",
    publicada: false,
    destaque: false,
    imagem: "bg-gradient-to-br from-blue-400 to-indigo-600",
  },
];

const CATEGORIA_CORES: Record<Categoria, string> = {
  Esportes: "bg-yellow-100 text-yellow-800",
  Sustentabilidade: "bg-green-100 text-green-800",
  Tecnologia: "bg-blue-100 text-blue-800",
  Cultura: "bg-purple-100 text-purple-800",
  Institucional: "bg-gray-100 text-gray-800",
  Saúde: "bg-rose-100 text-rose-800",
};

type Modo = "lista" | "novo" | "editar";

const noticia_vazia: Omit<Noticia, "id"> = {
  titulo: "",
  resumo: "",
  conteudo: "",
  data: new Date().toLocaleDateString("pt-BR"),
  categoria: "Institucional",
  publicada: false,
  destaque: false,
  imagem: "bg-gradient-to-br from-blue-400 to-indigo-600",
};

export function AdminPage() {
  const [noticias, setNoticias] = useState<Noticia[]>(noticias_iniciais);
  const [modo, setModo] = useState<Modo>("lista");
  const [noticiaEditando, setNoticiaEditando] = useState<Noticia | null>(null);
  const [form, setForm] = useState<Omit<Noticia, "id">>(noticia_vazia);
  const [busca, setBusca] = useState("");
  const [filtroCategoria, setFiltroCategoria] = useState<string>("todas");

  const noticiasFiltradas = noticias.filter((n) => {
    const matchBusca = n.titulo.toLowerCase().includes(busca.toLowerCase());
    const matchCategoria = filtroCategoria === "todas" || n.categoria === filtroCategoria;
    return matchBusca && matchCategoria;
  });

  const publicadas = noticias.filter((n) => n.publicada).length;
  const rascunhos = noticias.filter((n) => !n.publicada).length;
  const destaques = noticias.filter((n) => n.destaque).length;

  function abrirNovo() {
    setForm(noticia_vazia);
    setNoticiaEditando(null);
    setModo("novo");
  }

  function abrirEditar(noticia: Noticia) {
    setForm({ ...noticia });
    setNoticiaEditando(noticia);
    setModo("editar");
  }

  function cancelar() {
    setModo("lista");
    setNoticiaEditando(null);
  }

  function salvar() {
    if (!form.titulo.trim() || !form.resumo.trim()) return;

    if (modo === "novo") {
      const nova: Noticia = { ...form, id: Date.now() };
      setNoticias((prev) => [nova, ...prev]);
    } else if (modo === "editar" && noticiaEditando) {
      setNoticias((prev) =>
        prev.map((n) => (n.id === noticiaEditando.id ? { ...form, id: n.id } : n))
      );
    }
    setModo("lista");
  }

  function excluir(id: number) {
    setNoticias((prev) => prev.filter((n) => n.id !== id));
  }

  function togglePublicada(id: number) {
    setNoticias((prev) =>
      prev.map((n) => (n.id === id ? { ...n, publicada: !n.publicada } : n))
    );
  }

  function toggleDestaque(id: number) {
    setNoticias((prev) =>
      prev.map((n) => (n.id === id ? { ...n, destaque: !n.destaque } : n))
    );
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
          {/* Capa */}
          <div className="space-y-2">
            <Label className="flex items-center gap-2">
              <ImageIcon className="size-4 text-muted-foreground" />
              Imagem de Capa
            </Label>
            <div className="grid grid-cols-3 gap-2">
              {Object.entries(GRADIENTES).map(([cls, nome]) => (
                <button
                  key={cls}
                  onClick={() => setForm((f) => ({ ...f, imagem: cls }))}
                  className={`h-14 rounded-lg ${cls} transition-all ring-offset-2 ${
                    form.imagem === cls ? "ring-2 ring-primary" : "ring-0"
                  }`}
                  title={nome}
                />
              ))}
            </div>
          </div>

          <Separator />

          {/* Título */}
          <div className="space-y-2">
            <Label htmlFor="titulo">Título *</Label>
            <Input
              id="titulo"
              placeholder="Título da notícia"
              value={form.titulo}
              onChange={(e) => setForm((f) => ({ ...f, titulo: e.target.value }))}
            />
          </div>

          {/* Resumo */}
          <div className="space-y-2">
            <Label htmlFor="resumo" className="flex items-center gap-2">
              <AlignLeft className="size-4 text-muted-foreground" />
              Resumo *
            </Label>
            <Textarea
              id="resumo"
              placeholder="Breve descrição exibida na listagem"
              rows={2}
              className="resize-none"
              value={form.resumo}
              onChange={(e) => setForm((f) => ({ ...f, resumo: e.target.value }))}
            />
          </div>

          {/* Conteúdo */}
          <div className="space-y-2">
            <Label htmlFor="conteudo">Conteúdo Completo</Label>
            <Textarea
              id="conteudo"
              placeholder="Texto completo da notícia..."
              rows={6}
              className="resize-none"
              value={form.conteudo}
              onChange={(e) => setForm((f) => ({ ...f, conteudo: e.target.value }))}
            />
          </div>

          <div className="grid grid-cols-2 gap-4">
            {/* Categoria */}
            <div className="space-y-2">
              <Label className="flex items-center gap-2">
                <Tag className="size-4 text-muted-foreground" />
                Categoria
              </Label>
              <Select
                value={form.categoria}
                onValueChange={(v) => setForm((f) => ({ ...f, categoria: v as Categoria }))}
              >
                <SelectTrigger>
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  {CATEGORIAS.map((c) => (
                    <SelectItem key={c} value={c}>{c}</SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>

            {/* Data */}
            <div className="space-y-2">
              <Label htmlFor="data">Data de Publicação</Label>
              <Input
                id="data"
                placeholder="DD/MM/AAAA"
                value={form.data}
                onChange={(e) => setForm((f) => ({ ...f, data: e.target.value }))}
              />
            </div>
          </div>

          {/* Opções */}
          <div className="flex gap-4">
            <button
              type="button"
              onClick={() => setForm((f) => ({ ...f, publicada: !f.publicada }))}
              className={`flex items-center gap-2 px-4 py-2 rounded-lg border text-sm font-medium transition-colors ${
                form.publicada
                  ? "border-green-500 bg-green-50 text-green-700"
                  : "border-border text-muted-foreground hover:bg-accent"
              }`}
            >
              {form.publicada ? <Eye className="size-4" /> : <EyeOff className="size-4" />}
              {form.publicada ? "Publicada" : "Rascunho"}
            </button>
            <button
              type="button"
              onClick={() => setForm((f) => ({ ...f, destaque: !f.destaque }))}
              className={`flex items-center gap-2 px-4 py-2 rounded-lg border text-sm font-medium transition-colors ${
                form.destaque
                  ? "border-primary bg-primary/5 text-primary"
                  : "border-border text-muted-foreground hover:bg-accent"
              }`}
            >
              <Bell className="size-4" />
              Destacar no banner
            </button>
          </div>
        </CardContent>
      </Card>

      <div className="flex gap-3 justify-end">
        <Button variant="outline" onClick={cancelar}>Cancelar</Button>
        <Button
          onClick={salvar}
          disabled={!form.titulo.trim() || !form.resumo.trim()}
          className="gap-2"
        >
          <Check className="size-4" />
          {modo === "novo" ? "Publicar Notícia" : "Salvar Alterações"}
        </Button>
      </div>
    </div>
  );

  return (
    <div className="min-h-screen bg-muted/30">
      {/* Barra lateral */}
      <div className="flex">
        <aside className="hidden lg:flex flex-col w-56 min-h-[calc(100vh-57px)] bg-card border-r shrink-0">
          <nav className="flex-1 p-4 space-y-1">
            <p className="text-xs font-medium text-muted-foreground uppercase tracking-wider px-2 mb-3">
              Conteúdo
            </p>
            <button
              onClick={() => setModo("lista")}
              className={`w-full flex items-center gap-3 px-3 py-2 rounded-lg text-sm transition-colors ${
                modo === "lista" ? "bg-primary text-primary-foreground" : "hover:bg-accent text-foreground"
              }`}
            >
              <Newspaper className="size-4" />
              Notícias
            </button>
            <button className="w-full flex items-center gap-3 px-3 py-2 rounded-lg text-sm text-muted-foreground hover:bg-accent transition-colors opacity-50 cursor-not-allowed">
              <Bell className="size-4" />
              Avisos
            </button>
            <button className="w-full flex items-center gap-3 px-3 py-2 rounded-lg text-sm text-muted-foreground hover:bg-accent transition-colors opacity-50 cursor-not-allowed">
              <Calendar className="size-4" />
              Eventos
            </button>
          </nav>
          <div className="p-4 border-t">
            <Link to="/">
              <Button variant="ghost" size="sm" className="w-full justify-start gap-2 text-muted-foreground">
                <LogOut className="size-4" />
                Sair do painel
              </Button>
            </Link>
          </div>
        </aside>

        {/* Conteúdo principal */}
        <div className="flex-1 p-6 lg:p-8">
          {modo === "lista" ? (
            <div className="space-y-6">
              {/* Cabeçalho */}
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

              {/* Cards de resumo */}
              <div className="grid grid-cols-3 gap-4">
                <Card>
                  <CardContent className="pt-5 pb-4">
                    <p className="text-xs text-muted-foreground uppercase tracking-wider">Total</p>
                    <p className="text-3xl font-bold mt-1">{noticias.length}</p>
                    <p className="text-xs text-muted-foreground mt-1">notícias cadastradas</p>
                  </CardContent>
                </Card>
                <Card>
                  <CardContent className="pt-5 pb-4">
                    <p className="text-xs text-muted-foreground uppercase tracking-wider">Publicadas</p>
                    <p className="text-3xl font-bold mt-1 text-green-600">{publicadas}</p>
                    <p className="text-xs text-muted-foreground mt-1">{rascunhos} em rascunho</p>
                  </CardContent>
                </Card>
                <Card>
                  <CardContent className="pt-5 pb-4">
                    <p className="text-xs text-muted-foreground uppercase tracking-wider">Em Destaque</p>
                    <p className="text-3xl font-bold mt-1 text-primary">{destaques}</p>
                    <p className="text-xs text-muted-foreground mt-1">no banner principal</p>
                  </CardContent>
                </Card>
              </div>

              {/* Filtros */}
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
                <Select value={filtroCategoria} onValueChange={setFiltroCategoria}>
                  <SelectTrigger className="w-44">
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="todas">Todas as categorias</SelectItem>
                    {CATEGORIAS.map((c) => (
                      <SelectItem key={c} value={c}>{c}</SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>

              {/* Tabela de notícias */}
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
                          {/* Miniatura */}
                          <div className={`size-12 rounded-lg ${noticia.imagem} shrink-0`} />

                          {/* Informações */}
                          <div className="flex-1 min-w-0">
                            <div className="flex items-center gap-2 mb-0.5">
                              <p className="font-medium text-sm truncate">{noticia.titulo}</p>
                              {noticia.destaque && (
                                <Badge variant="default" className="text-xs shrink-0">Destaque</Badge>
                              )}
                            </div>
                            <div className="flex items-center gap-3 text-xs text-muted-foreground">
                              <span>{noticia.data}</span>
                              <span
                                className={`px-2 py-0.5 rounded-full text-xs font-medium ${
                                  CATEGORIA_CORES[noticia.categoria]
                                }`}
                              >
                                {noticia.categoria}
                              </span>
                            </div>
                          </div>

                          {/* Status */}
                          <button
                            onClick={() => togglePublicada(noticia.id)}
                            className={`flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-medium transition-colors shrink-0 ${
                              noticia.publicada
                                ? "bg-green-100 text-green-700 hover:bg-green-200"
                                : "bg-muted text-muted-foreground hover:bg-muted/80"
                            }`}
                          >
                            {noticia.publicada ? (
                              <><Eye className="size-3" /> Publicada</>
                            ) : (
                              <><EyeOff className="size-3" /> Rascunho</>
                            )}
                          </button>

                          {/* Ações */}
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
