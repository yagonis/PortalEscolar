import { Bell, Menu, Settings } from "lucide-react";
import { Link, NavLink } from "react-router";
import { Button } from "./ui/button";
import { Badge } from "./ui/badge";

export function Header() {
  return (
    <header className="border-b bg-card sticky top-0 z-50">
      {/* Barra de Acessibilidade */}
      <div className="bg-muted border-b">
        <div className="container mx-auto px-4 py-2 flex justify-end items-center gap-4 text-sm">
          <button className="hover:underline text-muted-foreground">Alto Contraste</button>
          <button className="hover:underline text-muted-foreground">Aumentar Fonte</button>
          <button className="hover:underline text-muted-foreground">VLibras</button>
        </div>
      </div>

      {/* Header Principal */}
      <div className="container mx-auto px-4">
        <div className="flex items-center justify-between py-4">
          {/* Logo e Nome */}
          <Link to="/" className="flex items-center gap-3">
            <div className="size-12 bg-primary rounded-lg flex items-center justify-center">
              <span className="text-primary-foreground font-bold text-xl">PE</span>
            </div>
            <div>
              <h1 className="font-bold text-lg leading-tight">Portal Escolar</h1>
              <p className="text-sm text-muted-foreground">Comunicação Colaborativa</p>
            </div>
          </Link>

          {/* Navegação Desktop */}
          <nav className="hidden lg:flex items-center gap-6">
            {[
              { to: "/", label: "Início" },
              { to: "/#avisos", label: "Avisos" },
              { to: "/#eventos", label: "Eventos" },
              { to: "/#noticias", label: "Notícias" },
              { to: "/#contato", label: "Fale Conosco" },
            ].map(({ to, label }) => (
              <NavLink
                key={label}
                to={to}
                className={({ isActive }) =>
                  `text-sm transition-colors hover:text-primary ${
                    isActive ? "text-primary font-medium" : "text-foreground"
                  }`
                }
              >
                {label}
              </NavLink>
            ))}
          </nav>

          {/* Ações */}
          <div className="flex items-center gap-2">
            <Button variant="ghost" size="icon" className="relative">
              <Bell className="size-5" />
            </Button>
            <Link to="/admin">
              <Button variant="outline" size="sm" className="gap-2 hidden lg:flex">
                <Settings className="size-4" />
                Painel Admin
              </Button>
            </Link>
            <Button variant="ghost" size="icon" className="lg:hidden">
              <Menu className="size-5" />
            </Button>
          </div>
        </div>
      </div>
    </header>
  );
}
