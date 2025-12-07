import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { describe, it, expect } from 'vitest'

/**
 * 샘플 컴포넌트 테스트 파일
 *
 * 목적:
 * 1. React 컴포넌트 테스트 프레임워크가 정상 작동하는지 확인
 * 2. @testing-library/react 사용 예시 제공
 * 3. 개발자를 위한 컴포넌트 테스트 작성 패턴 제공
 */

// 간단한 테스트용 컴포넌트
function SimpleButton({
  onClick,
  children,
}: {
  onClick: () => void
  children: React.ReactNode
}) {
  return <button onClick={onClick}>{children}</button>
}

function Greeting({ name }: { name: string }) {
  return <div>Hello, {name}!</div>
}

describe('Component Test Examples', () => {
  it('should render simple component', () => {
    const { container } = render(<div>Test Content</div>)
    expect(container.textContent).toBe('Test Content')
  })

  it('should render greeting component with props', () => {
    render(<Greeting name="Admin" />)
    expect(screen.getByText('Hello, Admin!')).toBeDefined()
  })

  it('should handle button click events', async () => {
    const user = userEvent.setup()
    let clicked = false
    const handleClick = () => {
      clicked = true
    }

    render(<SimpleButton onClick={handleClick}>Click Me</SimpleButton>)

    const button = screen.getByRole('button', { name: /click me/i })
    await user.click(button)

    expect(clicked).toBe(true)
  })

  it('should find elements by text', () => {
    render(
      <div>
        <h1>Title</h1>
        <p>Description</p>
      </div>
    )

    expect(screen.getByText('Title')).toBeDefined()
    expect(screen.getByText('Description')).toBeDefined()
  })

  it('should render list items', () => {
    const items = ['Item 1', 'Item 2', 'Item 3']

    render(
      <ul>
        {items.map(item => (
          <li key={item}>{item}</li>
        ))}
      </ul>
    )

    items.forEach(item => {
      expect(screen.getByText(item)).toBeDefined()
    })
  })
})

describe('Conditional Rendering Examples', () => {
  function ConditionalComponent({ show }: { show: boolean }) {
    return <div>{show ? <span>Visible</span> : <span>Hidden</span>}</div>
  }

  it('should render based on condition - true', () => {
    render(<ConditionalComponent show={true} />)
    expect(screen.getByText('Visible')).toBeDefined()
  })

  it('should render based on condition - false', () => {
    render(<ConditionalComponent show={false} />)
    expect(screen.getByText('Hidden')).toBeDefined()
  })
})
