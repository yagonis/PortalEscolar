import { Facebook, Instagram, Twitter, Mail, Phone, MapPin } from "lucide-react";

export function Footer() {
  return (
    <footer className="bg-card border-t mt-12">
      <div className="container mx-auto px-4 py-8">
        <div className="grid md:grid-cols-4 gap-8">
          {/* Sobre */}
          <div>
            <h3 className="font-bold mb-3">Portal Escolar</h3>
            <p className="text-sm text-muted-foreground">
              Plataforma colaborativa para comunicação entre escola e responsáveis.
            </p>
          </div>

          {/* Links Rápidos */}
          <div>
            <h4 className="font-medium mb-3">Links Rápidos</h4>
            <ul className="space-y-2 text-sm">
              <li><a href="#" className="text-muted-foreground hover:text-primary">Sobre a Escola</a></li>
              <li><a href="#" className="text-muted-foreground hover:text-primary">Calendário</a></li>
              <li><a href="#" className="text-muted-foreground hover:text-primary">Documentos</a></li>
              <li><a href="#" className="text-muted-foreground hover:text-primary">FAQ</a></li>
            </ul>
          </div>

          {/* Contato */}
          <div>
            <h4 className="font-medium mb-3">Contato</h4>
            <ul className="space-y-2 text-sm text-muted-foreground">
              <li className="flex items-center gap-2">
                <Phone className="size-4" />
                <span>(11) 1234-5678</span>
              </li>
              <li className="flex items-center gap-2">
                <Mail className="size-4" />
                <span>contato@escola.edu.br</span>
              </li>
              <li className="flex items-center gap-2">
                <MapPin className="size-4" />
                <span>Rua da Escola, 123</span>
              </li>
            </ul>
          </div>

          {/* Redes Sociais */}
          <div>
            <h4 className="font-medium mb-3">Redes Sociais</h4>
            <div className="flex gap-3">
              <a href="#" className="size-10 rounded-full bg-accent hover:bg-primary hover:text-primary-foreground flex items-center justify-center transition-colors">
                <Facebook className="size-5" />
              </a>
              <a href="#" className="size-10 rounded-full bg-accent hover:bg-primary hover:text-primary-foreground flex items-center justify-center transition-colors">
                <Instagram className="size-5" />
              </a>
              <a href="#" className="size-10 rounded-full bg-accent hover:bg-primary hover:text-primary-foreground flex items-center justify-center transition-colors">
                <Twitter className="size-5" />
              </a>
            </div>
          </div>
        </div>

        {/* Copyright */}
        <div className="border-t mt-8 pt-6 text-center text-sm text-muted-foreground">
          <p>&copy; 2026 Portal Escolar Colaborativo. Todos os direitos reservados.</p>
          <p className="mt-1">Desenvolvido com foco em transparência e participação democrática.</p>
        </div>
      </div>
    </footer>
  );
}
