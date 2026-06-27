import { Outlet, useLocation } from "react-router";
import { Header } from "./components/Header";
import { Footer } from "./components/Footer";

export function Root() {
  const location = useLocation();
  const isAdmin = location.pathname.startsWith("/admin");

  return (
    <div className="min-h-screen flex flex-col bg-background">
      <Header />
      <main className="flex-1">
        <Outlet />
      </main>
      {!isAdmin && <Footer />}
    </div>
  );
}
