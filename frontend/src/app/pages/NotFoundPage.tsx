import { Link } from "react-router";
import { Button } from "../components/ui/button";

export function NotFoundPage() {
  return (
    <div className="flex flex-col items-center justify-center min-h-[60vh] gap-4 text-center px-4">
      <p className="text-8xl font-bold text-muted-foreground/20">404</p>
      <h1 className="text-2xl font-semibold">Página não encontrada</h1>
      <p className="text-muted-foreground">O endereço acessado não existe neste portal.</p>
      <Link to="/">
        <Button>Voltar ao início</Button>
      </Link>
    </div>
  );
}
