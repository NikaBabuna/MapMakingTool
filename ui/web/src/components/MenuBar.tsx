"use client";

/*
 * File: ui/web/src/components/MenuBar.tsx
 * Purpose: Menu-bar row rendered from the menu descriptor model
 * Audience: MapTool
 * Update when: Menu interaction or chrome changes
 */

import { useEffect, useRef, useState } from "react";
import { MENUS, type MenuActionId, type MenuDescriptor } from "@/lib/menus";

type MenuBarProps = {
  menus?: MenuDescriptor[];
  onAction: (action: MenuActionId) => void;
  /** Check marks for `checkable` items, keyed by item id. */
  checked?: Record<string, boolean>;
};

export function MenuBar({ menus = MENUS, onAction, checked = {} }: MenuBarProps) {
  const [openId, setOpenId] = useState<string | null>(null);
  const barRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (openId === null) {
      return;
    }
    function handlePointerDown(e: PointerEvent) {
      if (!barRef.current?.contains(e.target as Node)) {
        setOpenId(null);
      }
    }
    function handleKey(e: KeyboardEvent) {
      if (e.key === "Escape") {
        setOpenId(null);
      }
    }
    window.addEventListener("pointerdown", handlePointerDown);
    window.addEventListener("keydown", handleKey);
    return () => {
      window.removeEventListener("pointerdown", handlePointerDown);
      window.removeEventListener("keydown", handleKey);
    };
  }, [openId]);

  function step(dir: -1 | 1) {
    const idx = menus.findIndex((m) => m.id === openId);
    if (idx < 0) {
      return;
    }
    const next = menus[(idx + dir + menus.length) % menus.length];
    setOpenId(next.id);
  }

  return (
    <div className="menu-bar" role="menubar" aria-label="Main menu" ref={barRef}>
      {menus.map((menu) => {
        const isOpen = openId === menu.id;
        return (
          <div className="menu-root" key={menu.id}>
            <button
              type="button"
              className={`menu-title${isOpen ? " is-open" : ""}`}
              role="menuitem"
              aria-haspopup="true"
              aria-expanded={isOpen}
              onClick={() => setOpenId(isOpen ? null : menu.id)}
              onPointerEnter={() => {
                if (openId !== null) {
                  setOpenId(menu.id);
                }
              }}
              onKeyDown={(e) => {
                if (e.key === "ArrowRight") {
                  e.preventDefault();
                  step(1);
                }
                if (e.key === "ArrowLeft") {
                  e.preventDefault();
                  step(-1);
                }
                if (e.key === "ArrowDown") {
                  e.preventDefault();
                  setOpenId(menu.id);
                }
              }}
            >
              {menu.label}
            </button>
            {isOpen ? (
              <div className="menu-list" role="menu" aria-label={menu.label}>
                {menu.items.map((item) => (
                  <button
                    key={item.id}
                    type="button"
                    role="menuitem"
                    className={`menu-item${item.separatorBefore ? " has-separator" : ""}`}
                    data-menu-item={item.id}
                    data-menu-stub={item.enabled ? undefined : "true"}
                    aria-disabled={item.enabled ? undefined : true}
                    disabled={!item.enabled}
                    onClick={() => {
                      if (!item.enabled || !item.action) {
                        return;
                      }
                      onAction(item.action);
                      setOpenId(null);
                    }}
                  >
                    <span className="menu-check" aria-hidden>
                      {item.checkable && checked[item.id] ? "✓" : ""}
                    </span>
                    <span className="menu-label">{item.label}</span>
                    <span className="menu-shortcut">{item.shortcut ?? ""}</span>
                  </button>
                ))}
              </div>
            ) : null}
          </div>
        );
      })}
    </div>
  );
}
